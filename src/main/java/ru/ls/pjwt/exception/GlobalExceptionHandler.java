package ru.ls.pjwt.exception;

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
import ru.ls.pjwt.exception.exceptions.ServerError;
import ru.ls.pjwt.utils.ApiResponse;
import ru.ls.pjwt.utils.constants.Exceptions;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
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
    public ResponseEntity<StandardResponse<ErrorResponse>> userExists(NotUniqueDataException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardResponse<ErrorResponse>> userExists(NoResourceFoundException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(HttpStatus.NOT_FOUND, "url not found");
    }

    @ExceptionHandler({ServerError.class, Exception.class})
    public ResponseEntity<StandardResponse<ErrorResponse>> serverError(Exception e) {
        log.error("internal server error (my): {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong... contact with a support to fix it");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<List<FieldErrorDto>>> validationException(MethodArgumentNotValidException e) {
        List<FieldErrorDto> errors = new ArrayList<>();
        e.getFieldErrors().forEach(fe ->
                errors.add(new FieldErrorDto(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage())));
        log.error("validation exception: {}. errors: {}", e.getMessage(), errors, e);
        return ApiResponse.failure(errors, Exceptions.VALIDATION, HttpStatus.BAD_REQUEST);
    }
}
