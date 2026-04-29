package ru.ls.pjwt.domain.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.ls.pjwt.security.config.BaseSecurityConfig;
import ru.ls.pjwt.security.filter.JwtFilter;

@Configuration
@Profile("dev")
public class DevSecurityConfig extends BaseSecurityConfig {

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  protected DevSecurityConfig(
      final JwtFilter jwtFilter,
      final AuthenticationEntryPoint authenticationEntryPoint,
      final AccessDeniedHandler accessDeniedHandler) {
    super(jwtFilter, authenticationEntryPoint, accessDeniedHandler);
  }

  @Bean
  @Order(1)
  public SecurityFilterChain devSecurityFilterChain(final HttpSecurity http) {
    return chain(http)
        .securityMatcher("/api/v1/test/**")
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers(HttpMethod.GET, "/api/v1/test/public")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .build();
  }
}
