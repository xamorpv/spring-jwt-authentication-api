package ru.ls.pjwt.domain.auth.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@ConfigurationProperties("dev.test-controller")
public record ControllerDevProperties(String publicResponse, String protectedResponse, String userOnlyResponse, String adminOnlyResponse) {
}
