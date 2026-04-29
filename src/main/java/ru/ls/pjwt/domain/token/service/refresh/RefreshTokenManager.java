package ru.ls.pjwt.domain.token.service.refresh;

import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;

/**
 * Performs low-level state changes on refresh tokens (marking as used, compromising) and handles
 * race conditions via optimistic locking.
 *
 * <p>Unlike {@link RefreshTokenService}, this class focuses on single-token state transitions and
 * does not perform validation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenManager {
  private final RefreshTokenRepository refreshTokenRepository;
  private final Clock clock;

  /**
   * Marks the given refresh token as used and immediately persists the change.
   *
   * @param refreshToken the token to mark as used
   * @throws RefreshTokenRaceConditionException if an optimistic locking conflict occurs
   */
  @SuppressWarnings("PMD.PreserveStackTrace")
  @Transactional
  public void use(final RefreshToken refreshToken) {
    try {
      log.debug("using token {}", refreshToken.getUuid());
      refreshToken.setUsed(true);
      refreshToken.setUsedAt(Instant.now(clock));
      refreshTokenRepository.saveAndFlush(refreshToken);
    } catch (OptimisticLockingFailureException e) {
      log.debug("race condition when using token: {}", e.getMessage(), e);
      throw new RefreshTokenRaceConditionException(refreshToken.getId());
    }
  }

  /**
   * Checks whether the given refresh token has already been used, and if so, compromises it along
   * with all remaining unused tokens of the same user.
   *
   * @param refreshToken the token to check
   * @return {@code true} if the token was already used (and has been compromised), {@code false} if
   *     the token is still unused
   */
  @Transactional
  public boolean compromiseIfUsed(final RefreshToken refreshToken) {
    if (refreshToken.isUsed()) {
      log.warn("token already used: {}", refreshToken.getUuid());
      if (refreshToken.isCompromised()) {
        log.warn("token replay detected, already compromised, ignoring");
      } else {
        log.warn("token replay not detected before; using all tokens for this user");
        refreshTokenRepository.useAndCompromiseTokensForUser(refreshToken.getUser().getUsername());
        refreshToken.setCompromised(true);
        refreshTokenRepository.save(refreshToken);
      }
      return true;
    }

    return false;
  }
}
