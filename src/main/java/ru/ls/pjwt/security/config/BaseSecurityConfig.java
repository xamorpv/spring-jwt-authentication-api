package ru.ls.pjwt.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ls.pjwt.common.web.api.HttpResponseWriter;
import ru.ls.pjwt.security.filter.JwtFilter;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public abstract class BaseSecurityConfig {
    private final JwtFilter jwtFilter;
    private final HttpResponseWriter httpResponseWriter;

    public HttpSecurity chain(HttpSecurity http) {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            log.warn("authentication entry point: {}", e.getMessage());
            log.trace("entry point exception: ", e);
            httpResponseWriter.writeError(response, HttpStatus.UNAUTHORIZED, e.getMessage() +
                    "; hint: maybe you forgot header Authorization: Bearer <token> to become authenticated");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            log.warn("access denied: {}", e.getMessage());
            log.trace("access denied exception: ", e);
            httpResponseWriter.writeError(response, HttpStatus.FORBIDDEN, "permission denied (you don't have authorities to use this endpoint)");
        };
    }
}
