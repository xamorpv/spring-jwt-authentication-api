package ru.ls.pjwt.common.web.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.web.api.ApiResponse;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.common.web.dto.exception.FieldErrorDto;
import ru.ls.pjwt.common.web.exception.NotUniqueDataException;
import ru.ls.pjwt.common.web.exception.ServerError;

import java.util.ArrayList;
import java.util.List;

/**
 * Глобальный обработчик ошибок (Fallback).
 * Имеет самый низкий приоритет по умолчанию.
 * Для специфичных доменных ошибок используйте локальные @ControllerAdvice с @Order(Ordered.HIGHEST_PRECEDENCE).
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ApiResponse apiResponse;
    private final ExceptionsProperties exceptionsProperties;

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> badCredentials(BadCredentialsException e) {
        log.warn("bad credentials: {}", e.getMessage());
        return apiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> accountStatus(AccountStatusException e) {
        log.warn("account status exception: {}", e.getMessage());
        return apiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(NotUniqueDataException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> notUniqueData(NotUniqueDataException e) {
        log.warn(e.getMessage());
        return apiResponse.error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> noResourceFound(NoResourceFoundException e) {
        log.warn(e.getMessage());
        return apiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ServerError.class, Exception.class})
    public ResponseEntity<StandardResponse<ErrorResponse>> serverError(Exception e) {
        log.error("internal server error: {}", e.getMessage(), e);
        return apiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong... contact with a support to fix it");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> validationException(MethodArgumentNotValidException e) {
        List<FieldErrorDto> errors = new ArrayList<>();
        e.getFieldErrors().forEach(fe ->
                errors.add(new FieldErrorDto(fe.getField(), fe.getDefaultMessage())));
        log.warn("validation exception: {}. errors: {}", e.getMessage(), errors);
        return apiResponse.errorInFields(HttpStatus.BAD_REQUEST, exceptionsProperties.validationFailed(), errors);
    }

    // Пробрасываем ошибки безопасности дальше, чтобы их обработал AccessDeniedHandler из SecurityConfig
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public void handleAccessDenied(RuntimeException e) {
        throw e;
    }
}
