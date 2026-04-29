package ru.ls.pjwt.common.web.api;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.time.TimeProvider;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class HttpResponseWriter {
  private final TimeProvider timeProvider;
  public final ObjectMapper objectMapper;

  /**
   * Writes an error response directly to the HTTP servlet response.
   *
   * <p>The response body is a JSON representation of a failed {@link StandardResponse} containing
   * an {@link ErrorResponse} with no field errors, the given status code, and the current UTC
   * timestamp.
   *
   * @param response the servlet response to write to
   * @param status the HTTP status to set on the response and include in the payload
   * @param message a human-readable error message
   * @throws IOException if an I/O error occurs while writing the response
   */
  public void writeError(
      final HttpServletResponse response, final HttpStatus status, final String message)
      throws IOException {
    writeValue(
        response,
        new StandardResponse<>(
            new ErrorResponse(status.value(), timeProvider.timestamp(), null), message, false),
        status.value());
  }

  private void writeValue(
      final HttpServletResponse response, final Object body, final int statusCode)
      throws IOException {
    response.setStatus(statusCode);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), body);
  }
}
