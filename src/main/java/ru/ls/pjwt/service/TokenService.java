package ru.ls.pjwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.AuthResponse;
import ru.ls.pjwt.dto.auth.LoginRequest;
import ru.ls.pjwt.model.JwtToken;
import ru.ls.pjwt.utils.constants.Jwt;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtService jwtService;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse refreshTokens(String token) {
        log.debug("refreshing tokens for {}", token);
        JwtToken jwtToken = new JwtToken(token);
        jwtToken.checkType(Jwt.REFRESH);
        String username = jwtToken.getUsername();
        authService.validateUsername(username);
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = refreshTokenService.updateRefreshToken(token);
        log.debug("successful refresh for {}", token);
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse createTokens(LoginRequest loginRequest) {
        log.info("creating tokens for login request {}", loginRequest.username());
        String username = authService.authenticate(loginRequest.username(), loginRequest.password()).getUsername();
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = refreshTokenService.createAndSaveToken(username);
        return new AuthResponse(accessToken, refreshToken);
    }
}
