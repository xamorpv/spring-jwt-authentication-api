package ru.ls.pjwt.common.web.handler;

import java.util.ArrayList;
import java.util.List;
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

/**
 * Global fallback exception handler for common application exceptions.
 *
 * <p>This handler has the lowest precedence by default. For domain-specific error handling, create
 * a separate {@code @ControllerAdvice} class with {@code @Order(Ordered.HIGHEST_PRECEDENCE)} to
 * override it.
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
  private final ApiResponse apiResponse;
  private final ExceptionsProperties exceptionsProperties;

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<StandardResponse<ErrorResponse>> badCredentials(
      final BadCredentialsException badCredentialsException) {
    log.warn("bad credentials: {}", badCredentialsException.getMessage());
    return apiResponse.error(HttpStatus.UNAUTHORIZED, badCredentialsException.getMessage());
  }

  @ExceptionHandler(AccountStatusException.class)
  public ResponseEntity<StandardResponse<ErrorResponse>> accountStatus(
      final AccountStatusException accountStatusException) {
    log.warn("account status exception: {}", accountStatusException.getMessage());
    return apiResponse.error(HttpStatus.UNAUTHORIZED, accountStatusException.getMessage());
  }

  @ExceptionHandler(NotUniqueDataException.class)
  public ResponseEntity<StandardResponse<ErrorResponse>> notUniqueData(
      final NotUniqueDataException notUniqueDataException) {
    log.warn("not unique data exception: {}", notUniqueDataException.getMessage());
    return apiResponse.error(HttpStatus.CONFLICT, notUniqueDataException.getMessage());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<StandardResponse<ErrorResponse>> noResourceFound(
      final NoResourceFoundException noResourceFoundException) {
    log.warn("resource not found: {}", noResourceFoundException.getMessage());
    return apiResponse.error(HttpStatus.NOT_FOUND, noResourceFoundException.getMessage());
  }

  @ExceptionHandler({ServerError.class, Exception.class})
  public ResponseEntity<StandardResponse<ErrorResponse>> serverError(final Exception exception) {
    log.error("internal server error: {}", exception.getMessage(), exception);
    return apiResponse.error(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "something went wrong... contact with a support to fix it");
  }

  @SuppressWarnings("PMD.LongVariable")
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardResponse<ErrorResponse>> validationException(
      final MethodArgumentNotValidException methodArgumentNotValidException) {
    final List<FieldErrorDto> errors = new ArrayList<>();
    methodArgumentNotValidException
        .getFieldErrors()
        .forEach(
            fe ->
                errors.add(
                    new FieldErrorDto(
                        fe.getField(),
                        (fe.getDefaultMessage() != null
                            ? fe.getDefaultMessage()
                            : "Invalid value"))));
    log.warn(
        "validation exception: {}. errors: {}",
        methodArgumentNotValidException.getMessage(),
        errors);
    return apiResponse.errorInFields(
        HttpStatus.BAD_REQUEST, exceptionsProperties.validationFailed(), errors);
  }

  // Пробрасываем ошибки безопасности дальше, чтобы их обработал AccessDeniedHandler из
  // SecurityConfig
  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  public void handleAccessDenied(final RuntimeException runtimeException) {
    throw runtimeException;
  }
}
