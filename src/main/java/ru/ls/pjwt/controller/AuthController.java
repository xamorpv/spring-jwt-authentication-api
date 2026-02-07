package ru.ls.pjwt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ls.pjwt.dto.StandardResponse;
import ru.ls.pjwt.dto.auth.response.AuthResponse;
import ru.ls.pjwt.dto.auth.request.LoginRequest;
import ru.ls.pjwt.dto.auth.request.RefreshTokenRequest;
import ru.ls.pjwt.dto.auth.request.RegisterRequest;
import ru.ls.pjwt.dto.auth.response.RegisterResponse;
import ru.ls.pjwt.service.AuthService;
import ru.ls.pjwt.service.RefreshTokenService;
import ru.ls.pjwt.service.TokenService;
import ru.ls.pjwt.utils.ApiResponse;

//todo токены в httpOnlyCookies, а не в dto, который может посмотреть js
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<StandardResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("handling login request {}", loginRequest.username());
        return ApiResponse.success(tokenService.createTokens(loginRequest), "authenticated successfully", HttpStatus.OK);
    }

    //пока что для простоты через dto
    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<AuthResponse>> refresh(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        log.info("handling refresh request {}", refreshTokenRequest);
        return ApiResponse.success(tokenService.refreshTokens(refreshTokenRequest.refreshToken()), "successful refresh", HttpStatus.OK);
    }

    @PostMapping("/invalidate-refresh-token")
    public ResponseEntity<StandardResponse<Void>> logout(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        log.info("invalidating token {}", refreshTokenRequest);
        refreshTokenService.deleteToken(refreshTokenRequest.refreshToken());
        return ApiResponse.success("token deleted successfully", HttpStatus.OK);
    }


    @PostMapping("/register")
    public ResponseEntity<StandardResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("handling register request {} - {}", registerRequest.username(), registerRequest.email());
        return ApiResponse.success(authService.register(registerRequest), "registered successfully", HttpStatus.OK);
    }
}