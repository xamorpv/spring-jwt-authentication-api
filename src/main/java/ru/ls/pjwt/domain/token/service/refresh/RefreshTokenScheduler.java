package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenScheduler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Scheduled(fixedDelay = 1000L * 60 * 60 * 24 * 7)
    @Transactional
    public void clearRefreshTokens() {
        log.debug("clearing tokens");
        refreshTokenRepository.deleteUsedBefore(Instant.now().minus(jwtProperties.getRefreshTokenExpirationDays()+30, ChronoUnit.DAYS));
    }
}
