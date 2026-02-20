package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedDelay = 1000L * 60 * 60 * 24 * 7)
    protected void clearRefreshTokens() {
        log.debug("clearing tokens");
        refreshTokenRepository.deleteUsedBefore(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
