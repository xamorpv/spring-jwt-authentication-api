package ru.ls.pjwt.properties;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@ConfigurationProperties("pjwt.jwt")
@Getter
public class JwtProperties {
    private final SecretKey secretKey;
    private final String secretKeyString;
    private final String accessToken;
    private final String refreshToken;
    private final int accessTokenExpirationMinutes;
    private final int refreshTokenExpirationDays;

    public JwtProperties(String secretKeyString, String accessToken, String refreshToken, int accessTokenExpirationMinutes, int refreshTokenExpirationDays) {
        this.secretKeyString = secretKeyString;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;

        secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }
}
