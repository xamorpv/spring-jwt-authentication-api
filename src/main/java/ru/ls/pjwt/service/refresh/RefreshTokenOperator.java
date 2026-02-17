package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenOperator {
    private final RefreshTokenRepository refreshTokenRepository;

    public void useAndCompromise(RefreshToken refreshToken) {
        use(refreshToken);
        compromise(refreshToken);
    }

    public void compromise(RefreshToken refreshToken) {
        log.debug("compromising token {}", refreshToken);
        refreshToken.setCompromised(true);
        use(refreshToken);
    }

    public void use(RefreshToken refreshToken) {
        log.debug("using token {}", refreshToken);
        refreshToken.setUsed(true);
        refreshToken.setUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }
}
