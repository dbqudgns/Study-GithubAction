package com.happiness.budtree.domain.member.service;

import com.happiness.budtree.jwt.JWTUtil;
import com.happiness.budtree.util.ApiResponse;
import com.happiness.budtree.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutService {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.Unauthorized("토큰이 없거나 Bearer 로 시작하지 않음");
        }

        String access = token.split(" ")[1];

        if (!access.equals(redisUtil.getData("AT:" + jwtUtil.getUsername(access)))) {
            return ApiResponse.Unauthorized("해당 Access 토큰의 값이 Redis에 저장되지 않았습니다.");
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for(Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }

        }

        if (refresh == null)
            return ApiResponse.Unauthorized("Refresh 토큰이 없습니다.");


        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return ApiResponse.Unauthorized("Refresh Token이 만료되었습니다. 다시 로그인하세요.");
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh"))
           return ApiResponse.Unauthorized("Refresh 토큰이 아닙니다.");


        String username = jwtUtil.getUsername(refresh);
        String redisRT = redisUtil.getData("AT:" + username);

        if (redisRT == null)
           return ApiResponse.Unauthorized("Redis에 해당 Refresh 토큰이 없습니다.");
        

        //로그아웃 진행
        redisUtil.deleteData("RT:" + username);

        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        
        //Access Token 블랙리스트
        long remainingTime = jwtUtil.getRemainingTime(access); // ms 반환
        if (remainingTime == 0L) {
            return ResponseEntity.ok(ApiResponse.success(200, "로그아웃 완료"));
        }

        remainingTime /= 1000L; // s 처리
        redisUtil.setBlackList("AT:" + username, access, remainingTime);


        return ResponseEntity.ok(ApiResponse.success(200, "로그아웃 완료"));
    }
}
