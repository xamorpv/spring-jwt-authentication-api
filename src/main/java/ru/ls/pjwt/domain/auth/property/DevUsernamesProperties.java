package ru.ls.pjwt.domain.auth.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@ConfigurationProperties("dev.test-users-names")
public record DevUsernamesProperties(String user, String moderator, String admin) {
}
