package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenOperator {
    private final RefreshTokenRepository refreshTokenRepository;

    public void compromise(RefreshToken refreshToken) {
        log.debug("compromising token {}", refreshToken);
        refreshToken.setCompromised(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void use(RefreshToken refreshToken) {
        log.debug("using token {}", refreshToken);
        refreshToken.setUsed(true);
        refreshToken.setUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }
}
