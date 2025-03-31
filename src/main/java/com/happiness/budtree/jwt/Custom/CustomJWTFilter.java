package com.happiness.budtree.jwt.Custom;

import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.member.Role;
import com.happiness.budtree.jwt.JWTUtil;
import com.happiness.budtree.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//로그인된 사용자의 JWT 토큰을 검증하는 필터
@RequiredArgsConstructor
@Slf4j
public class CustomJWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        log.info("{}", path);

        //Security 필터를 적용하지 않을 경로 지정
        return pathMatcher.match("/swagger-ui/**", path) ||
                pathMatcher.match("/v3/api-docs/**", path) ||
                pathMatcher.match("/member/register", path) ||
                pathMatcher.match("/member/login", path) ||
                pathMatcher.match("/member/check", path) ||
                pathMatcher.match("/member/reissue", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            log.info("토큰이 없거나 Bearer 로 시작하지 않음");
            request.setAttribute("exceptionMessage", "Access 토큰이 없습니다. 로그인을 진행해주세요.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String accessToken = token.split(" ")[1];

        try {

            String username = jwtUtil.getUsername(accessToken);

            //Redis에 저장된 Access 토큰인지 확인
            if (!redisUtil.getData("AT:" + username).equals(accessToken)) {
                log.info("Redis에 등록되지 않은 토큰 사용 시도: {}", username);
                request.setAttribute("exceptionMessage", "해당 Access 토큰은 Redis에 저장되지 않았습니다.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //블랙리스트 확인 (로그아웃된 Access 토큰인지 확인)
            String blackListData = redisUtil.getBlackListData("AT:" + username);
            if (blackListData != null && blackListData.equals(accessToken)) {
                log.info("블랙리스트에 등록된 토큰 사용 시도: {}", username);
                request.setAttribute("exceptionMessage", "로그아웃을 진행한 Access Token입니다.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // JWT 토큰 만료 검사
            jwtUtil.isExpired(accessToken);

            String category = jwtUtil.getCategory(accessToken);
            if (!category.equals("access")) {
                request.setAttribute("exceptionMessage", "Access 토큰이 아닙니다.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String role = jwtUtil.getRole(accessToken);
            Role userRole = Role.valueOf(role.replace("ROLE_", ""));

            Member member = Member.builder()
                    .username(username)
                    .role(userRole)
                    .password("temp_pw")
                    .build();

            CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);

            //스프링 시큐리티 인증 토큰 생성 : 인증된 사용자 객체, 비밀번호, 사용자 권한 목록
            Authentication authToken = new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());

            //세션에 사용자 등록 : 이후 컨트롤러에서 @AuthenticationPrincipal 등을 사용해 현재 로그인한 사용자를 가져올 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);


        } catch (ExpiredJwtException e) {

            log.error("Access 토큰이 만료됐습니다. Refresh 토큰을 통해 재발급 요청을 해주세요.: {}", e.getMessage());
            request.setAttribute("exceptionMessage", "Access 토큰이 만료됐습니다. Refresh 토큰을 통해 재발급 요청을 해주세요.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        } catch (UnsupportedJwtException e) { // 서버에서 지원하지 않는 알고리즘으로 생성된 JWT일때 발생

            log.error("해당 JWT는 지원하지 않습니다.: {}", e.getMessage());
            request.setAttribute("exceptionMessage", "해당 JWT는 지원하지 않습니다.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        } catch (SignatureException e) { // JWT의 생성 시점 SecretKey와 복호화 시점의 SecretKey가 달라 발생

            log.error("JWT의 서명이 올바르지 않습니다.: {}", e.getMessage());
            request.setAttribute("exceptionMessage", "JWT의 서명이 올바르지 않습니다.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        } catch (MalformedJwtException e) { // JWT의 구조가 올바르지 않을 때 발생

            log.error("JWT의 구조가 올바르지 않습니다.: {}", e.getMessage());
            request.setAttribute("exceptionMessage", "JWT의 구조가 올바르지 않습니다.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        }

    }
}
