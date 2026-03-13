package com.example.carrental.controller;

import com.example.carrental.dto.AuthRequestDto;
import com.example.carrental.dto.AuthResponseDto;
import com.example.carrental.dto.RegisterRequestDto;
import com.example.carrental.exception.BadRequestException;
import com.example.carrental.exception.TooManyRequestsException;
import com.example.carrental.security.RateLimitService;
import com.example.carrental.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final long REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60;

    private static final int LOGIN_MAX_REQUESTS = 5;
    private static final int REFRESH_MAX_REQUESTS = 10;
    private static final long RATE_LIMIT_WINDOW_SECONDS = 60;

    private final AuthService authService;
    private final RateLimitService rateLimitService;

    @Value("${app.security.cookie-secure:false}")
    private boolean cookieSecure;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto requestDto,
            HttpServletResponse response) {

        AuthService.AuthResult result = authService.register(requestDto);
        addRefreshTokenCookie(response, result.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(result.getResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody AuthRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response) {

        String clientIp = getClientIp(request);
        boolean allowed = rateLimitService.isAllowed(
                "login:" + clientIp,
                LOGIN_MAX_REQUESTS,
                RATE_LIMIT_WINDOW_SECONDS
        );

        if (!allowed) {
            throw new TooManyRequestsException("Too many login attempts. Please try again later.");
        }

        AuthService.AuthResult result = authService.login(requestDto);
        addRefreshTokenCookie(response, result.getRefreshToken());

        return ResponseEntity.ok(result.getResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {

        String clientIp = getClientIp(request);
        boolean allowed = rateLimitService.isAllowed(
                "refresh:" + clientIp,
                REFRESH_MAX_REQUESTS,
                RATE_LIMIT_WINDOW_SECONDS
        );

        if (!allowed) {
            throw new TooManyRequestsException("Too many refresh attempts. Please try again later.");
        }

        if (refreshToken == null) {
            throw new BadRequestException("Refresh token missing");
        }

        AuthService.AuthResult result = authService.refresh(refreshToken);
        addRefreshTokenCookie(response, result.getRefreshToken());

        return ResponseEntity.ok(result.getResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.noContent().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api/auth")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}