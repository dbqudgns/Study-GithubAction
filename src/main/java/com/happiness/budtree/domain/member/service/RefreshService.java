package com.happiness.budtree.domain.member.service;

import com.happiness.budtree.jwt.CookieUtil;
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
public class RefreshService {

    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Transactional
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {

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
            return ApiResponse.Unauthorized("자동 로그아웃 되었습니다. 다시 로그인하세요.");
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh"))
            return ApiResponse.Unauthorized("Refresh 토큰이 아닙니다.");


        String username = jwtUtil.getUsername(refresh);
        String redisRT = redisUtil.getData("RT:" + username);

        if (redisRT == null)
            return ApiResponse.Unauthorized("Redis에 해당 Refresh 토큰이 없습니다.");


        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJWT("access", username, role, 60 * 10 * 1000L); //10분 설정
        redisUtil.setDataExpire("AT:" + username, newAccess, 60 * 10L); //redis에 AT 저장


        String newRefresh = jwtUtil.createJWT("refresh", username, role, 24 * 60 * 60 * 1000L); //24시간 설정
        redisUtil.setDataExpire("RT:" + username, newRefresh, 24 * 60 * 60L); //redis에 RT 저장

        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(CookieUtil.createCookie("refresh", newRefresh, 24 * 60 * 60));

        return ResponseEntity.ok(ApiResponse.SuccessOrFail(200, "Access 토큰 재발급 및 Refresh Rotate 성공"));

    }
}
