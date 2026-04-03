package ru.ls.pjwt.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.dto.tokens.JwtClaims;
import ru.ls.pjwt.dto.api.request.LoginRequest;
import ru.ls.pjwt.dto.api.response.LoginResponse;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.service.user.AuthService;
import ru.ls.pjwt.service.user.UserSecurity;
import ru.ls.pjwt.service.token.jwt.JwtClaimsFactory;
import ru.ls.pjwt.service.token.jwt.JwtFactory;
import ru.ls.pjwt.service.token.refresh.RefreshTokenFactory;
import ru.ls.pjwt.service.token.refresh.RefreshTokenService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtFactory jwtFactory;
    private final AuthService authService;
    private final RefreshTokenFactory refreshTokenFactory;
    private final JwtClaimsFactory claimsFactory;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserSecurity userSecurity;
    private final UserMapper userMapper;

    @Transactional
    public LoginResponse refreshTokens(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        log.debug("refreshing tokens for username={}", jwtClaims.username());
        User user = userSecurity.validateUsername(jwtClaims.username());
        String accessToken = accessTokenService.createAccessToken(jwtClaims, user);
        String refreshToken = refreshTokenFactory.rotateRefreshToken(jwtClaims, user);
        log.debug("successful refresh for {}", jwtClaims.username());
        return new LoginResponse(refreshToken, accessToken);
    }

    @Transactional
    public LoginResponse createTokens(LoginRequest loginRequest) {
        log.info("creating tokens for login request {}", loginRequest.username());
        User user = authService.authenticate(loginRequest.username(), loginRequest.password());
        String accessToken = jwtFactory.createAccessToken(userMapper.userEntityToUserDetails(user));
        String refreshToken = refreshTokenFactory.createAndSaveToken(user);
        return new LoginResponse(refreshToken, accessToken);
    }

    public void invalidateRefreshToken(String refreshToken) {
        refreshTokenService.markTokenAsUsed(refreshToken);
    }
}
