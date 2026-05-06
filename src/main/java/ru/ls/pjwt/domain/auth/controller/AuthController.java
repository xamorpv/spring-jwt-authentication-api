package ru.ls.pjwt.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ls.pjwt.common.web.api.ApiResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RefreshTokenRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.auth.service.AuthService;

// todo токены в httpOnlyCookies, а не в dto, который может посмотреть js
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
  private final AuthService authService;
  private final ApiResponse apiResponse;

  @PostMapping("/login")
  public ResponseEntity<StandardResponse<LoginResponse>> login(
      @RequestBody @Valid final LoginRequest loginRequest) {
    log.info("handling login request {}", loginRequest.username());
    return apiResponse.success(
        authService.login(loginRequest), "authenticated successfully", HttpStatus.OK);
  }

  // пока что для простоты через dto
  @PostMapping("/refresh")
  public ResponseEntity<StandardResponse<LoginResponse>> refresh(
      @RequestBody @Valid final RefreshTokenRequest refreshTokenRequest) {
    log.info("handling refresh request");
    return apiResponse.success(
        authService.refresh(refreshTokenRequest.refreshToken()),
        "successful refresh",
        HttpStatus.OK);
  }

  @PostMapping("/invalidate-refresh-token")
  public ResponseEntity<StandardResponse<Void>> invalidateRefreshToken(
      @RequestBody @Valid final RefreshTokenRequest refreshTokenRequest) {
    log.info("invalidating token");
    authService.logout(refreshTokenRequest.refreshToken());
    return apiResponse.success("token deleted successfully", HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<StandardResponse<RegisterResponse>> register(
      @RequestBody @Valid final RegisterRequest registerRequest) {
    log.info(
        "handling register request {} - {}", registerRequest.username(), registerRequest.email());
    return apiResponse.success(
        authService.register(registerRequest), "registered successfully", HttpStatus.CREATED);
  }
}
