package com.andreamolteni.economia_familiare.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class AuthCookieService {

    private final JwtService jwtService;

    @Value("${app.security.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.security.cookie-samesite}")
    private String cookieSameSite;

    public AuthCookieService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/auth")
                .maxAge(Duration.ofSeconds(jwtService.refreshTokenMaxAgeSeconds()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
