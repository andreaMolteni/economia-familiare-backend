package com.andreamolteni.economia_familiare.controller;

import com.andreamolteni.economia_familiare.security.AuthCookieService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;
    private final AuthCookieService authCookieService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          JwtDecoder jwtDecoder,
                          UserDetailsService userDetailsService,
                          AuthCookieService authCookieService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req,  HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.userName(), req.password())
            );

            var principal = (UserDetails) auth.getPrincipal();
            var roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            String username = principal.getUsername();
            String accessToken = jwtService.generateAccessToken(username, roles);
            String newRefresh = jwtService.generateRefreshToken(username);

            authCookieService.setRefreshCookie(response, newRefresh);
            return new LoginResponse(accessToken, "Bearer", jwtService.accessTokenExpiresInSeconds());
        } catch (BadCredentialsException ex) {
            // 401 pulito
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");
        }

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (!"refresh".equals(jwt.getClaimAsString("typ"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = jwt.getSubject();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String newAccess = jwtService.generateAccessToken(username, roles);

        String newRefresh = jwtService.generateRefreshToken(username);

        authCookieService.setRefreshCookie(response, newRefresh);

        return new LoginResponse(
                newAccess,
                "Bearer",
                jwtService.accessTokenExpiresInSeconds()
        );
    }
}

