package ru.ls.pjwt.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.response.AuthResponse;
import ru.ls.pjwt.dto.auth.request.LoginRequest;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.service.jwt.JwtClaimsFactory;
import ru.ls.pjwt.service.jwt.JwtFactory;
import ru.ls.pjwt.service.refresh.RefreshTokenFactory;
import ru.ls.pjwt.utils.LogUtils;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtFactory jwtFactory;
    private final AuthService authService;
    private final RefreshTokenFactory refreshTokenFactory;
    private final JwtClaimsFactory claimsFactory;
    private final AccessTokenService accessTokenService;

    public AuthResponse refreshTokens(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        log.debug("refreshing tokens for {}", LogUtils.safeUserDetails(jwtClaims.getUserDetails()));
        String accessToken = accessTokenService.createAccessToken(jwtClaims);
        String refreshToken = refreshTokenFactory.updateRefreshToken(jwtClaims);
        log.debug("successful refresh for {}", jwtClaims.getUsername());
        return new AuthResponse(refreshToken, accessToken);
    }

    public AuthResponse createTokens(LoginRequest loginRequest) {
        log.info("creating tokens for login request {}", loginRequest.username());
        UserDetails userDetails = authService.authenticate(loginRequest.username(), loginRequest.password());
        String accessToken = jwtFactory.createAccessToken(userDetails);
        String refreshToken = refreshTokenFactory.createAndSaveToken(userDetails.getUsername());
        return new AuthResponse(refreshToken, accessToken);
    }
}
