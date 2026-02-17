package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshTokenScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    private void clearRefreshTokens() {
        log.debug("clearing tokens");
        refreshTokenRepository.deleteUsedLater(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
