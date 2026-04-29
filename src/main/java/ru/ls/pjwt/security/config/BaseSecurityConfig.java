package ru.ls.pjwt.security.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ls.pjwt.security.filter.JwtFilter;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EnableWebSecurity
public class BaseSecurityConfig {
  private final JwtFilter jwtFilter;
  private final AuthenticationEntryPoint authenticationEntryPoint;
  private final AccessDeniedHandler accessDeniedHandler;

  /**
   * Configures common HTTP security settings that are shared across all {@link SecurityFilterChain}
   * beans.
   *
   * <p>The method applies:
   *
   * <ul>
   *   <li>CSRF disabled
   *   <li>stateless session management
   *   <li>the JWT filter before {@link UsernamePasswordAuthenticationFilter}
   *   <li>a custom {@link AuthenticationEntryPoint} and {@link AccessDeniedHandler}
   * </ul>
   *
   * <p>Subclasses can call this method to obtain a pre-configured {@link HttpSecurity} and then add
   * more specific rules (e.g. endpoint matchers) before building the final chain.
   *
   * @param http the {@code HttpSecurity} to modify
   * @return the modified {@code HttpSecurity} for further customization
   */
  public HttpSecurity chain(final HttpSecurity http) {
    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
