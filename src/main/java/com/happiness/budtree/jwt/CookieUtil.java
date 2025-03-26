package com.happiness.budtree.jwt;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createCookie(String key, String value, Integer expired) {

        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(expired);
        //cookie.setSecure(true); HTTPS에서만 쿠키를 전송하도록 설정 => 배포 시 주석 해제

        return cookie;

    }
}
