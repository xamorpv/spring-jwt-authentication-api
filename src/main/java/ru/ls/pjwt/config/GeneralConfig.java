package ru.ls.pjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.ls.pjwt.utils.pathMatchers.FluentOrRequestMatcher;
import ru.ls.pjwt.utils.pathMatchers.RequestMatcherUtils;

@Configuration
public class GeneralConfig {

    @Bean
    public RequestMatcher requestMatcher() {
        return RequestMatcherUtils.createOrRequestMatherFluent(
                new FluentOrRequestMatcher()
                        .or(HttpMethod.GET, "/api/v1/auth/login", "/api/v1/auth/refresh",
                                "/api/v1/auth/invalidate-refresh-token", "/api/v1/auth/register",
                                "/api/v1/test/public")
        );
    }
}
