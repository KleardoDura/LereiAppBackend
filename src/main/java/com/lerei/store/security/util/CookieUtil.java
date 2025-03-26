package com.lerei.store.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {
    @Value("${host}")
    public static String hostFromValue;

    public static void create(HttpServletResponse httpServletResponse, String name, String value, Boolean secure, Integer maxAge, String domain){
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(true);
	cookie.setMaxAge(maxAge);
        cookie.setDomain(domain);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }
    public static void clear(HttpServletResponse httpServletResponse, String name){
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true); 
        cookie.setMaxAge(1);
        //cookie.setDomain("lereimusic.com");
        cookie.setDomain(hostFromValue);
        httpServletResponse.addCookie(cookie);
    }
}
