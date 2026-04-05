package ru.ls.pjwt.domain.token.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.web.api.ApiResponse;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class TokenExceptionHandler {
    private final ApiResponse apiResponse;
    private final RefreshTokenRaceConditionExceptionHandler refreshTokenRaceConditionExceptionHandler;
    private final ExceptionsProperties exceptionsProperties;

    @ExceptionHandler(JwtTokenRequestException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> jwtException(JwtTokenRequestException e) {
        log.warn("jwt token failure: {}", e.getMessage());
        return apiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(RefreshTokenRaceConditionException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> onTokenRaceCondition(RefreshTokenRaceConditionException e) {
        refreshTokenRaceConditionExceptionHandler.handleException(e);
        return apiResponse.error(HttpStatus.UNAUTHORIZED, exceptionsProperties.refreshTokenCompromised());
    }
}
