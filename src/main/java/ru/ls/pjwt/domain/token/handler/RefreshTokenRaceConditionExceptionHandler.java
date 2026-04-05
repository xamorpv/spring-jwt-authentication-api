package ru.ls.pjwt.domain.token.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;
import ru.ls.pjwt.domain.token.service.refresh.RefreshTokenManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenRaceConditionExceptionHandler {
    private final RefreshTokenManager refreshTokenManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void handleException(RefreshTokenRaceConditionException e) {
        RefreshToken compromisedRefreshToken = refreshTokenRepository.findById(e.getTokenId()).orElseThrow(
                () -> new JwtTokenRequestException("token not found"));
        log.warn("RefreshToken race condition with {}", compromisedRefreshToken.getUuid());
        refreshTokenManager.compromiseIfUsed(compromisedRefreshToken);
    }
}
