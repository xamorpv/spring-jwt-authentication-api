package ru.ls.pjwt.service.refresh;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenOperator {
    private final RefreshTokenRepository refreshTokenRepository;

    public void useAndCompromise(RefreshToken refreshToken) {
        compromise(refreshToken);
        use(refreshToken);
    }

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
