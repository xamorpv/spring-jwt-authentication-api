package ru.ls.pjwt.domain.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.service.AuthService;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.service.jwt.JwtFactory;
import ru.ls.pjwt.domain.token.service.jwt.JwtParser;
import ru.ls.pjwt.domain.token.service.refresh.RefreshTokenFactory;
import ru.ls.pjwt.domain.token.service.refresh.RefreshTokenService;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.mapper.UserToDetailsMapper;
import ru.ls.pjwt.domain.user.service.UserService;
import ru.ls.pjwt.domain.user.service.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
  private final JwtFactory jwtFactory;
  private final JwtParser jwtParser;

  private final AccessTokenService accessTokenService;

  private final RefreshTokenFactory refreshTokenFactory;
  private final RefreshTokenService refreshTokenService;

  private final AuthService authService;

  private final UserValidator userValidator;
  private final UserService userService;
  private final UserToDetailsMapper userToDetailsMapper;

  @Transactional
  public LoginResponse refreshTokens(String token) {
    JwtClaims jwtClaims = jwtParser.parseRefreshToken(token);
    log.debug("refreshing tokens for username={}", jwtClaims.username());
    User user = userService.findUserByUsername(jwtClaims.username());
    userValidator.validateAccountStatus(user);
    String accessToken = accessTokenService.createAccessToken(jwtClaims, user);
    String refreshToken = refreshTokenFactory.rotateRefreshToken(jwtClaims, user);
    log.debug("successful refresh for {}", jwtClaims.username());
    return new LoginResponse(refreshToken, accessToken);
  }

  @Transactional
  public LoginResponse createTokens(LoginRequest loginRequest) {
    log.info("creating tokens for login request {}", loginRequest.username());
    User user = authService.authenticate(loginRequest.username(), loginRequest.password());
    String accessToken =
        jwtFactory.createAccessToken(userToDetailsMapper.userEntityToUserDetails(user));
    String refreshToken = refreshTokenFactory.createAndSaveToken(user);
    return new LoginResponse(refreshToken, accessToken);
  }

  @Transactional
  public void invalidateRefreshToken(String token) {
    JwtClaims jwtClaims = jwtParser.parseRefreshToken(token);
    log.info("invalidating refresh token: {}", jwtClaims.uuid());
    refreshTokenService.markTokenAsUsed(jwtClaims);
  }
}
