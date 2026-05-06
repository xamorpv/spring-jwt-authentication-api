package ru.ls.pjwt.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.ls.pjwt.security.filter.JwtFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends BaseSecurityConfig {

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  protected SecurityConfig(
      final JwtFilter jwtFilter,
      final AuthenticationEntryPoint authenticationEntryPoint,
      final AccessDeniedHandler accessDeniedHandler) {
    super(jwtFilter, authenticationEntryPoint, accessDeniedHandler);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
    return chain(http)
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers("/api/v1/auth/**", "/actuator/health")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .build();
  }
}
