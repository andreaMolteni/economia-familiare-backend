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

    @Value("${app.security.refresh-cookie-name}")
    private String cookieName;

    @Value("${app.security.refresh-cookie-path}")
    private String cookiePath;

    @Value("${app.security.refresh-cookie-days}")
    private long cookieDays;

    @Value("${app.security.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.security.cookie-samesite}")
    private String cookieSameSite;

    public AuthCookieService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        String sameSite = cookieSameSite == null ? "Lax" : cookieSameSite.trim();

        // Normalizza per sicurezza
        if (!sameSite.equals("None") && !sameSite.equals("Lax") && !sameSite.equals("Strict")) {
            sameSite = "Lax";
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(cookiePath)
                .maxAge(Duration.ofSeconds(cookieDays))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
