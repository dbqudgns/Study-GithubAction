package com.happiness.budtree.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//JWT 토큰 발급 및 토큰 파싱, 유효기간 검증을 수행하는 클래스
@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil( @Value("${JWT_KEY}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public long getRemainingTime(String token) {
        Date expiration = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
        long remainingTime = expiration.getTime() - System.currentTimeMillis();
        return Math.max(remainingTime, 0L); //만료된 경우 0 반환
    }

    public String createJWT(String category, String username, String role, Long expired) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expired))
                .signWith(secretKey)
                .compact();

    }

}
