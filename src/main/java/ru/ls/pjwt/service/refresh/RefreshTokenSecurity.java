package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.repository.RefreshTokenRepository;
import ru.ls.pjwt.service.TransactionManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenSecurity {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TransactionManager transactionManager;

    public void checkUsed(RefreshToken refreshToken) {
        if (refreshToken.getUsed()) {
            log.warn("token already used; using all tokens for this user");
            transactionManager.executeInNonRollbackableTransaction(() ->
                    refreshTokenRepository.findActiveByUsername(refreshToken.getUser().getUsername()).forEach(this::use));
            throw new JwtTokenRequestException("refresh token was compromised. you may be get hacked. please re-login");
        }
    }

    public void use(RefreshToken refreshToken) {
        log.debug("using token {}", refreshToken);
        refreshToken.setUsed(true);
        refreshToken.setUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    private void clearRefreshTokens() {
        log.debug("clearing tokens");
        refreshTokenRepository.deleteUsedLater(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
