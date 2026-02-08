package ru.ls.pjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.ls.pjwt.utils.pathMatchers.FluentOrRequestMatcher;
import ru.ls.pjwt.utils.pathMatchers.RequestMatcherUtils;

@Configuration
public class GeneralConfig {

    @Bean
    public RequestMatcher requestMatcher() {
        return RequestMatcherUtils.createOrRequestMatherFluent(
                new FluentOrRequestMatcher()
                        .or(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh",
                                "/api/v1/auth/invalidate-refresh-token", "/api/v1/auth/register")
                        .or(HttpMethod.GET, "/api/v1/test/public")
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }
}
