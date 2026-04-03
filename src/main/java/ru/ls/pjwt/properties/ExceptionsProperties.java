package ru.ls.pjwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.exceptions")
public record ExceptionsProperties(String badCredentials, String accountExpired, String accountLocked,
                                   String credentialsExpired, String accountDisabled, String userExists,
                                   String emailExists, String emailOrUserExists, String validationFailed, String refreshTokenCompromised) {
}
