package ru.ls.pjwt.domain.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.auth.service.AuthService;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.dto.TokenPair;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;
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
  private final UserValidator userValidator;
  private final UserService userService;
  private final UserToDetailsMapper userToDetailsMapper;

  /**
   * Refreshes both access and refresh tokens.
   *
   * <p>Parses the refresh token, validates the user status, generates a new access token and
   * rotates the refresh token.
   *
   * @param token the raw refresh token
   * @return a pair of new access and refresh tokens
   * @throws JwtTokenRequestException if the refresh token is invalid or expired
   * @throws BadCredentialsException if the user does not exist
   * @throws AccountStatusException if the user account is locked/disabled/expired
   * @throws RefreshTokenRaceConditionException if a concurrent rotation is detected
   */
  @Transactional
  public TokenPair refreshTokens(final String token) {
    final JwtClaims jwtClaims = jwtParser.parseRefreshToken(token);
    log.debug("refreshing tokens for username={}", jwtClaims.username());
    final User user = userService.findUserByUsername(jwtClaims.username());
    userValidator.validateAccountStatus(user);
    final String accessToken = accessTokenService.createAccessToken(jwtClaims, user);
    final String refreshToken = refreshTokenFactory.rotateRefreshToken(jwtClaims, user);
    return new TokenPair(refreshToken, accessToken);
  }

  /**
   * Creates a new access/refresh token pair for the given user.
   *
   * <p>This method does NOT check passwords – the caller (e.g. {@link AuthService}) must ensure the
   * user is already authenticated.
   *
   * @param user the authenticated user entity (with eagerly loaded authorities)
   * @return the generated token pair
   */
  @Transactional
  public TokenPair createTokens(final User user) {
    final String accessToken =
        jwtFactory.createAccessToken(userToDetailsMapper.userEntityToUserDetails(user));
    final String refreshToken = refreshTokenFactory.createAndSaveToken(user);
    return new TokenPair(refreshToken, accessToken);
  }

  /**
   * Marks the refresh token (identified by the raw JWT) as used, so it cannot be used again.
   *
   * @param token the raw refresh token string
   * @throws JwtTokenRequestException if the token is invalid or its type is incorrect
   */
  @Transactional
  public void invalidateRefreshToken(final String token) {
    final JwtClaims jwtClaims = jwtParser.parseRefreshToken(token);
    refreshTokenService.markTokenAsUsed(jwtClaims);
  }
}
