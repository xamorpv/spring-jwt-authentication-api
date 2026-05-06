package ru.ls.pjwt.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.ls.pjwt.common.web.api.HttpResponseWriter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityHandlersConfig {
  private final HttpResponseWriter httpResponseWriter;

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, e) -> {
      log.warn("authentication entry point: {}", e.getMessage());
      log.trace("entry point exception: ", e);
      httpResponseWriter.writeError(
          response,
          HttpStatus.UNAUTHORIZED,
          e.getMessage()
              + "; hint: maybe you forgot header Authorization: "
              + "Bearer <token> to become authenticated");
    };
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, e) -> {
      log.warn("access denied: {}", e.getMessage());
      log.trace("access denied exception: ", e);
      httpResponseWriter.writeError(
          response,
          HttpStatus.FORBIDDEN,
          "permission denied (you don't have authorities to use this endpoint)");
    };
  }
}
