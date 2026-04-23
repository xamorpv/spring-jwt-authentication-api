package ru.ls.pjwt.domain.token.service.refresh;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenScheduler {
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtProperties jwtProperties;
  private final Clock clock;

  @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 7, initialDelay = 1000 * 15)
  @Transactional
  public void clearRefreshTokens() {
    int count =
        refreshTokenRepository.deleteUsedBefore(
            Instant.now(clock)
                .minus(jwtProperties.getRefreshTokenExpirationDays() + 30, ChronoUnit.DAYS));
    log.info("cleared {} tokens", count);
  }
}
