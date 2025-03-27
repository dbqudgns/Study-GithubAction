package com.happiness.budtree.jwt.Custom;

import com.happiness.budtree.domain.member.Member;
import com.happiness.budtree.domain.member.Role;
import com.happiness.budtree.jwt.JWTUtil;
import com.happiness.budtree.util.ApiResponse;
import com.happiness.budtree.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//로그인된 사용자의 JWT 토큰을 검증하는 필터
@RequiredArgsConstructor
@Slf4j
public class CustomJWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (request.getRequestURI().equals("/member/logout")) {
            log.info("logout 호출");
            filterChain.doFilter(request, response);

            return;
        }

        if (token == null || !token.startsWith("Bearer ")) {
            log.info("토큰이 없거나 Bearer 로 시작하지 않음");
            filterChain.doFilter(request, response);

            return;
        }

        String accessToken = token.split(" ")[1];

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            ApiResponse.Unauthorized(response, "Access 토큰 만료됐습니다. Refresh 토큰을 통해 재발급 요청을 해주세요.");

            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            ApiResponse.Unauthorized(response, "Access 토큰이 아닙니다.");

            return;
        }

        String username = jwtUtil.getUsername(accessToken);

        String blackListData = redisUtil.getBlackListData("AT:" + username);
        if (blackListData != null && blackListData.equals(accessToken)) {
            ApiResponse.Unauthorized(response, "로그아웃한 Access Token입니다.");

            return;
        }

        /**
        //Redis에 저장되어 있는 Access token과 Header로 온 Access Token 값 같은지 확인
        //로그인 하고 다른 기기로 로그인 하면 로그인이 안된다. 즉, 다중 로그인이 안된다.
        //why ? Redis에서 아이디 중복으로 처리 안함
        if (!accessToken.equals(redisUtil.getData("AT:" + jwtUtil.getUsername(accessToken)))) {

            ApiResponse.Unauthorized(response, "해당 Access 토큰의 값이 Redis에 저장되지 않았습니다.");

            return;
        }
         **/

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

    }
}
