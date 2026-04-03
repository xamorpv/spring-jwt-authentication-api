package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenManager {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void compromise(RefreshToken refreshToken) {
        log.debug("compromising token {}", refreshToken);
        refreshToken.setCompromised(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void use(RefreshToken refreshToken) {
        try {
            log.debug("using token {}", refreshToken);
            refreshToken.setUsed(true);
            refreshToken.setUsedAt(Instant.now());
            refreshTokenRepository.saveAndFlush(refreshToken);
        } catch (OptimisticLockingFailureException e) {
            log.debug("race condition when using token: {}", e.getMessage(), e);
            throw new RefreshTokenRaceConditionException(refreshToken.getId());
        }
    }

    @Transactional
    public boolean compromiseIfUsed(RefreshToken refreshToken) {
        if (refreshToken.isUsed()) {
            log.warn("token already used: {}", refreshToken);
            if (refreshToken.isCompromised()) {
                log.debug("token already compromised; throw exception and do nothing");
            } else {
                log.warn("token was not compromised before; using all tokens for this user");
                refreshTokenRepository.useAndCompromiseTokensForUser(refreshToken.getUser().getUsername());
                refreshToken.setCompromised(true);
                refreshTokenRepository.save(refreshToken);
            }
            return true;
        }

        return false;
    }
}
