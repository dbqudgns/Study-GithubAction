package com.happiness.budtree.domain.member.service;

import com.happiness.budtree.domain.member.DTO.request.MemberLoginRQ;
import com.happiness.budtree.jwt.CookieUtil;
import com.happiness.budtree.jwt.JWTUtil;
import com.happiness.budtree.util.ApiResponse;
import com.happiness.budtree.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    public ResponseEntity<?> login(MemberLoginRQ memberLoginRequest, HttpServletResponse response) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginRequest.username(), memberLoginRequest.password()));

            //로그인 성공 시
            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            String access = jwtUtil.createJWT("access", username, role, 60 * 10 * 1000L); //10분 설정
            redisUtil.setDataExpire("AT:" + memberLoginRequest.username(), access, 60 * 10L); //redis에 AT 저장


            String refresh = jwtUtil.createJWT("refresh", username, role, 24 * 60 * 60 * 1000L); //24시간 설정
            redisUtil.setDataExpire("RT:" + memberLoginRequest.username(), refresh, 24 * 60 * 60L); //redis에 RT 저장

            response.setHeader("Authorization", "Bearer " + access); //AT는 헤더로 전송
            response.addCookie(CookieUtil.createCookie("refresh", refresh, 24 * 60 * 60)); //RT는 쿠키로 전송

            return ResponseEntity.ok(ApiResponse.success(200, "로그인 성공"));

        } catch (AuthenticationException e) {

            //로그인 실패 시
            Map<String, Object> errorResponse = ApiResponse.success(401, "아이디 또는 비밀번호가 올바르지 않습니다.");

            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(errorResponse);

        }
    }
}
