package com.andreamolteni.economia_familiare.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import com.andreamolteni.economia_familiare.dto.LoginRequest;
import com.andreamolteni.economia_familiare.dto.LoginResponse;
import com.andreamolteni.economia_familiare.security.JwtService;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          JwtDecoder jwtDecoder,
                          UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req,  HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.userName(), req.password())
            );

            var principal = (UserDetails) auth.getPrincipal();
            var roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

            String accessToken = jwtService.generateAccessToken(principal.getUsername(), roles);
            String refreshToken = jwtService.generateRefreshToken(principal.getUsername());

            ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(false)      // ✅ in dev; in prod true (https)
                    .sameSite("Lax")    // ok per dev
                    .path("/auth")
                    .maxAge(Duration.ofDays(7))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return new LoginResponse(accessToken, "Bearer", jwtService.accessTokenExpiresInSeconds());
        } catch (BadCredentialsException ex) {
            // 401 pulito
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken,
                                 HttpServletResponse response) {
        System.out.println("HIT /auth/refresh - cookie present? " + (refreshToken != null && !refreshToken.isBlank()));
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");
        }

        Jwt jwt = jwtDecoder.decode(refreshToken);

        if (!"refresh".equals(jwt.getClaimAsString("typ"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = jwt.getSubject();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String newAccess = jwtService.generateAccessToken(username, roles);

        return new LoginResponse(newAccess, "Bearer", jwtService.refreshTokenMaxAgeSeconds());
    }
}

