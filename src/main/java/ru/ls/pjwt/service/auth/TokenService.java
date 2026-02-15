package ru.ls.pjwt.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.response.AuthResponse;
import ru.ls.pjwt.dto.auth.request.LoginRequest;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.service.jwt.JwtClaimsFactory;
import ru.ls.pjwt.service.jwt.JwtFactory;
import ru.ls.pjwt.service.refresh.RefreshTokenFactory;
import ru.ls.pjwt.utils.LogUtils;
import ru.ls.pjwt.utils.constants.Jwt;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtFactory jwtFactory;
    private final AuthService authService;
    private final RefreshTokenFactory refreshTokenFactory;
    private final JwtClaimsFactory claimsFactory;

    public AuthResponse refreshTokens(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        log.debug("refreshing tokens for {}", LogUtils.safeUserDetails(jwtClaims.getUserDetails()));
        jwtClaims.checkType(Jwt.REFRESH);
        String username = jwtClaims.getUsername();
        authService.validateUsername(username);
        String accessToken = jwtFactory.createAccessToken(username);
        String refreshToken = refreshTokenFactory.updateRefreshToken(token);
        log.debug("successful refresh for {}", jwtClaims.getUsername());
        return new AuthResponse(refreshToken, accessToken);
    }

    public AuthResponse createTokens(LoginRequest loginRequest) {
        log.info("creating tokens for login request {}", loginRequest.username());
        String username = authService.authenticate(loginRequest.username(), loginRequest.password()).getUsername();
        String accessToken = jwtFactory.createAccessToken(username);
        String refreshToken = refreshTokenFactory.createAndSaveToken(username);
        return new AuthResponse(refreshToken, accessToken);
    }
}
