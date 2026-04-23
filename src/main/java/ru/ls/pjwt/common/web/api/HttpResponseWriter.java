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

  public void writeError(HttpServletResponse response, HttpStatus status, String message)
      throws IOException {
    writeValue(
        response,
        new StandardResponse<>(
            new ErrorResponse(status.value(), timeProvider.timestamp(), null), message, false),
        status.value());
  }

  private void writeValue(HttpServletResponse response, Object body, int statusCode)
      throws IOException {
    response.setStatus(statusCode);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), body);
  }
}
