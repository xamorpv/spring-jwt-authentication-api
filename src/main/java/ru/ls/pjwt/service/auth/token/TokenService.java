package ru.ls.pjwt.service.auth.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.dto.auth.request.LoginRequest;
import ru.ls.pjwt.dto.auth.response.AuthResponse;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.service.auth.AuthService;
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
    private final UserMapper userMapper;

    public AuthResponse refreshTokens(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        log.debug("refreshing tokens for {}", LogUtils.safeUserDetails(jwtClaims.userDetails()));
        String accessToken = accessTokenService.createAccessToken(jwtClaims);
        String refreshToken = refreshTokenFactory.rotateRefreshToken(jwtClaims);
        log.debug("successful refresh for {}", jwtClaims.username());
        return new AuthResponse(refreshToken, accessToken);
    }

    public AuthResponse createTokens(LoginRequest loginRequest) {
        log.info("creating tokens for login request {}", loginRequest.username());
        User user = authService.authenticate(loginRequest.username(), loginRequest.password());
        String accessToken = jwtFactory.createAccessToken(userMapper.userEntityToUserDetails(user));
        String refreshToken = refreshTokenFactory.createAndSaveToken(user);
        return new AuthResponse(refreshToken, accessToken);
    }
}
