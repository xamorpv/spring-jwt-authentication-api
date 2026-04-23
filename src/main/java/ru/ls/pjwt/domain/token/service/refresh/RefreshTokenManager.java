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

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenManager {
  private final RefreshTokenRepository refreshTokenRepository;
  private final Clock clock;

  @Transactional
  public void use(RefreshToken refreshToken) {
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

  @Transactional
  public boolean compromiseIfUsed(RefreshToken refreshToken) {
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
