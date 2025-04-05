package com.happiness.budtree.jwt;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createCookie(String key, String value, Integer expired) {

        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(expired);
        cookie.setSecure(true);

        return cookie;

    }
}
