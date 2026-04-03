package ru.ls.pjwt.domain.token.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.web.api.ApiResponse;
import ru.ls.pjwt.common.web.api.dto.ErrorResponse;
import ru.ls.pjwt.common.web.api.dto.StandardResponse;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.exception.RefreshTokenRaceConditionException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class TokenExceptionHandler {
    private final ApiResponse apiResponse;
    private final RefreshTokenRaceConditionExceptionHandler refreshTokenRaceConditionExceptionHandler;
    private final ExceptionsProperties exceptionsProperties;

    @ExceptionHandler(JwtTokenRequestException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> jwtException(JwtTokenRequestException e) {
        log.error("jwt token exception: {}", e.getMessage(), e);
        return apiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(RefreshTokenRaceConditionException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> onTokenRaceCondition(RefreshTokenRaceConditionException e) {
        refreshTokenRaceConditionExceptionHandler.handleException(e);
        return apiResponse.error(HttpStatus.UNAUTHORIZED, exceptionsProperties.refreshTokenCompromised());
    }
}
