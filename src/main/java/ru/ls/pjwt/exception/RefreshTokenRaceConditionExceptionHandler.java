package ru.ls.pjwt.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.exception.exceptions.RefreshTokenRaceConditionException;
import ru.ls.pjwt.repository.RefreshTokenRepository;
import ru.ls.pjwt.service.refresh.RefreshTokenOperator;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenRaceConditionExceptionHandler {
    private final RefreshTokenOperator refreshTokenOperator;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void handleException(RefreshTokenRaceConditionException e) {
        refreshTokenOperator.compromiseIfUsed(refreshTokenRepository.findById(e.getTokenId()).orElseThrow(
                () -> new JwtTokenRequestException("token not found")));
    }
}
