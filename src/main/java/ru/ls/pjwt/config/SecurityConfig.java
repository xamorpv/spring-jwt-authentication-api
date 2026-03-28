package ru.ls.pjwt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ls.pjwt.filter.JwtFilter;
import ru.ls.pjwt.properties.AuthoritiesProperties;
import ru.ls.pjwt.utils.JsonApiResponse;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthoritiesProperties authoritiesProperties;
    private final JwtFilter jwtFilter;
    private final JsonApiResponse jsonApiResponse;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh",
                                "/api/v1/auth/invalidate-refresh-token", "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/test/public", "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/test/user-only").hasAuthority(authoritiesProperties.user())
                        .requestMatchers(HttpMethod.GET, "/api/v1/test/protected").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            log.error("authentication entry point: {}", e.getMessage(), e);
            jsonApiResponse.writeError(response, HttpStatus.UNAUTHORIZED, e.getMessage() +
                    "; hint: maybe you forgot header Authorization: Bearer <token> to become authenticated");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            log.error("access denied: {}", e.getMessage(), e);
            jsonApiResponse.writeError(response, HttpStatus.FORBIDDEN, "permission denied (you don't have authorities to use this endpoint)");
        };
    }
}
