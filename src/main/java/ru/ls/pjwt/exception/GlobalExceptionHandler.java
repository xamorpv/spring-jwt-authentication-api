package ru.ls.pjwt.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.ls.pjwt.dto.ErrorResponse;
import ru.ls.pjwt.dto.FieldErrorDto;
import ru.ls.pjwt.dto.StandardResponse;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.exception.exceptions.NotUniqueDataException;
import ru.ls.pjwt.exception.exceptions.RefreshTokenRaceConditionException;
import ru.ls.pjwt.exception.exceptions.ServerError;
import ru.ls.pjwt.utils.ApiResponse;
import ru.ls.pjwt.utils.constants.Exceptions;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final RefreshTokenRaceConditionExceptionHandler refreshTokenRaceConditionExceptionHandler;

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> badCredentials(BadCredentialsException e) {
        log.error("bad credentials: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(JwtTokenRequestException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> jwtException(JwtTokenRequestException e) {
        log.error("jwt token exception: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> accountStatus(AccountStatusException e) {
        log.error("account status exception: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(NotUniqueDataException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> notUniqueData(NotUniqueDataException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> noResourceFound(NoResourceFoundException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ServerError.class, Exception.class})
    public ResponseEntity<StandardResponse<ErrorResponse>> serverError(Exception e) {
        log.error("internal server error: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong... contact with a support to fix it");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> validationException(MethodArgumentNotValidException e) {
        List<FieldErrorDto> errors = new ArrayList<>();
        e.getFieldErrors().forEach(fe ->
                errors.add(new FieldErrorDto(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage())));
        log.error("validation exception: {}. errors: {}", e.getMessage(), errors, e);
        return ApiResponse.errorInFields(HttpStatus.BAD_REQUEST, Exceptions.VALIDATION, errors);
    }

    @ExceptionHandler(RefreshTokenRaceConditionException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> onTokenRaceCondition(RefreshTokenRaceConditionException e) {
        refreshTokenRaceConditionExceptionHandler.handleException(e);
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, Exceptions.REFRESH_TOKEN_COMPROMISED);
    }
}
