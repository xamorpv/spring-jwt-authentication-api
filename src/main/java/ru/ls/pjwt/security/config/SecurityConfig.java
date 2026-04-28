package ru.ls.pjwt.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ru.ls.pjwt.common.web.api.HttpResponseWriter;
import ru.ls.pjwt.security.filter.JwtFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends BaseSecurityConfig {

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public SecurityConfig(JwtFilter jwtFilter, HttpResponseWriter httpResponseWriter) {
    super(jwtFilter, httpResponseWriter);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
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
