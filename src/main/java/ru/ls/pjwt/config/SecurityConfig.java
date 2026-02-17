package ru.ls.pjwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.ls.pjwt.filter.JwtFilter;
import ru.ls.pjwt.utils.constants.Authorities;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final RequestMatcher requestMatcher;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http) {
        return http
                .userDetailsService(userDetailsService)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                                .requestMatchers(HttpMethod.GET, "/api/v1/test/user-only").hasAuthority(Authorities.USER)
                        .requestMatchers(requestMatcher).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/test/protected").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
