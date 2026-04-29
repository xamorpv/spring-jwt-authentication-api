package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;

/**
 * Validates the state of a refresh token before it is used.
 *
 * <p>This class checks whether the token has been used and invokes the compromise logic if
 * necessary, isolating the validation concern from the rest of the refresh token processing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenValidator {
  private final ExceptionsProperties exceptionsProperties;
  private final RefreshTokenManager refreshTokenManager;

  /**
   * Checks whether the given refresh token has already been used.
   *
   * <p><b>Warning:</b> This method produces side effects. If token replay is detected (the token
   * was already used), it automatically compromises all remaining unused tokens for this user to
   * prevent session hijacking.
   *
   * @param refreshToken the refresh token to check
   * @throws JwtTokenRequestException if the token has already been used (whether previously
   *     compromised or just now compromised)
   */
  @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      noRollbackFor = JwtTokenRequestException.class)
  public void checkUsed(final RefreshToken refreshToken) {
    // Logic explanation:
    // If the token was used, we check if it was already compromised.
    // If not compromised yet, it means this is the first token replay attempt.
    // Since we don't know who used it first (the real user or an attacker),
    // we must securely invalidate ALL tokens for this user.
    if (refreshTokenManager.compromiseIfUsed(refreshToken)) {
      throw new JwtTokenRequestException(exceptionsProperties.refreshTokenCompromised());
    }
  }
}
