package ru.ls.pjwt.common.web.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.time.TimeProvider;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.common.web.dto.exception.FieldErrorDto;

@Component
@RequiredArgsConstructor
public class ApiResponse {
  private final TimeProvider timeProvider;

  /**
   * Creates an error response with no field errors.
   *
   * @param status HTTP status code for the response
   * @param message error description
   * @return a {@link ResponseEntity} with a failed {@link StandardResponse} wrapping an {@link
   *     ErrorResponse}
   */
  public ResponseEntity<StandardResponse<ErrorResponse>> error(
      HttpStatus status, @Nullable String message) {
    String safeMessage = message != null ? message : status.getReasonPhrase();
    return errorInFields(status, safeMessage, null);
  }

  /**
   * Creates an error response containing field errors.
   *
   * @param status HTTP status code for the response
   * @param message error description
   * @param errors field errors list to include in {@link ErrorResponse}
   * @return a {@link ResponseEntity} with a failed {@link StandardResponse} wrapping an {@link
   *     ErrorResponse} that carries the given field errors
   */
  public ResponseEntity<StandardResponse<ErrorResponse>> errorInFields(
      HttpStatus status, String message, @Nullable List<FieldErrorDto> errors) {
    return failure(
        new ErrorResponse(status.value(), timeProvider.timestamp(), errors), message, status);
  }

  /**
   * Creates a failed {@link StandardResponse} containing the given data.
   *
   * @param <T> type of the data
   * @param data object to include in the response body
   * @param message error description
   * @param status HTTP status code for the response
   * @return a {@link ResponseEntity} with a failed {@link StandardResponse} wrapping the provided
   *     {@code data}
   */
  public <T> ResponseEntity<StandardResponse<T>> failure(
      T data, String message, HttpStatus status) {
    return ResponseEntity.status(status).body(new StandardResponse<>(data, message, false));
  }

  /**
   * Creates a successful {@link StandardResponse} containing the given data.
   *
   * @param <T> type of the data
   * @param data object to include in the response body
   * @param message error description
   * @param status HTTP status code for the response
   * @return a {@link ResponseEntity} with a successful {@link StandardResponse} wrapping the
   *     provided {@code data}
   */
  public <T> ResponseEntity<StandardResponse<T>> success(
      T data, String message, HttpStatus status) {
    return ResponseEntity.status(status).body(new StandardResponse<>(data, message, true));
  }

  /**
   * Creates a successful {@link StandardResponse} with no data (Void).
   *
   * @param message description (e.g. success message)
   * @param status HTTP status code for the response
   * @return a {@link ResponseEntity} with a successful {@link StandardResponse} that carries no
   *     body data (Void)
   */
  public ResponseEntity<StandardResponse<Void>> success(String message, HttpStatus status) {
    return ResponseEntity.status(status).body(new StandardResponse<>(null, message, true));
  }
}
