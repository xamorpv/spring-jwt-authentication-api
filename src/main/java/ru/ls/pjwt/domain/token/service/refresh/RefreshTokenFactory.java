package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.token.dto.CreatedRefreshToken;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;
import ru.ls.pjwt.domain.token.service.jwt.JwtFactory;
import ru.ls.pjwt.domain.user.entity.User;

/**
 * Creates new refresh tokens and persists them, delegating token building to {@link JwtFactory} and
 * persistence to {@link RefreshTokenService}.
 *
 * <p>This class is responsible for the "creation" part of the refresh token lifecycle, including
 * token rotation (updating an existing token with a new UUID).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenFactory {
  private final JwtFactory jwtFactory;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenManager refreshTokenManager;

  /**
   * Creates a new refresh token and persists it for the given user.
   *
   * @param user the user for whom the refresh token is created
   * @return the compact refresh token string
   */
  public String createAndSaveToken(final User user) {
    log.debug("saving token for user: {}", user.getUsername());
    final CreatedRefreshToken createdRefreshToken =
        jwtFactory.createRefreshToken(user.getUsername());
    final RefreshToken refreshToken = refreshTokenService.save(createdRefreshToken, user);
    log.debug(
        "refresh token saved for username {}, token: {}",
        user.getUsername(),
        refreshToken.getUuid());
    return createdRefreshToken.token();
  }

  /**
   * Rotates an existing refresh token, marking the previous one as used and persisting a new token
   * for the specified user.
   *
   * @param token the parsed claims of the current refresh token
   * @param user the user who owns this token
   * @return the new refresh token string
   * @throws BadCredentialsException if the token UUID is missing (legacy token)
   * @throws JwtTokenRequestException if the token is not found or has already been used
   * @throws RefreshTokenRaceConditionException if a concurrent rotation is detected
   */
  @Transactional
  public String rotateRefreshToken(final JwtClaims token, final User user) {
    // todo grace period
    // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а
    // вернуть тот, что был выдан меньше 30 секунд назад)

    log.debug("updating token with uuid={} for username={}", token.uuid(), user.getUsername());
    refreshTokenManager.use(refreshTokenService.getToken(token));
    final CreatedRefreshToken newToken = jwtFactory.updateRefreshToken(token);
    final RefreshToken refreshToken = refreshTokenService.save(newToken, user);
    log.debug("refresh token updated: {}", refreshToken.getUuid());
    return newToken.token();
  }
}
