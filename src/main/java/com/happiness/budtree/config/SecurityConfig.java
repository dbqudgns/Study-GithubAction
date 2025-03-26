package com.happiness.budtree.config;

import com.happiness.budtree.domain.member.Role;
import com.happiness.budtree.jwt.Custom.CustomJWTFilter;
import com.happiness.budtree.jwt.JWTUtil;
import com.happiness.budtree.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Bean //사용자 비밀번호 암호화를 위해 필요
    public BCryptPasswordEncoder passwordEncoder() throws Exception {
        return new BCryptPasswordEncoder();
    }

    @Bean //AuthenticationManager 동적 생성을 위해 필요
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //disable 설정
        http
                .httpBasic((basic) -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/", "/member/login", "/member/logout", "/member/check", "/member/register", "/member/reissue").permitAll()
                        .requestMatchers("/member/profile/**", "/chatroom/**", "/message/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .anyRequest().authenticated());

        http
                .addFilterBefore(new CustomJWTFilter(jwtUtil, redisUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //CORS 설정
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                       CorsConfiguration config = new CorsConfiguration();
                       config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                       config.setAllowedMethods(Collections.singletonList("*"));
                       config.setAllowCredentials(true);
                       config.setAllowedHeaders(Collections.singletonList("*"));
                       config.setMaxAge(3600L);
                       config.setExposedHeaders(Collections.singletonList("Authorization"));

                       return config;
                    }
                })));


        return http.build();
    }

}
