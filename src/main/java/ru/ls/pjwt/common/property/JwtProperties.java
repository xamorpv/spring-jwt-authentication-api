package ru.ls.pjwt.common.property;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@ConfigurationProperties("pjwt.jwt")
@Getter
public class JwtProperties {
    private final SecretKey secretKey;
    private final String accessToken;
    private final String refreshToken;
    private final int accessTokenExpirationMinutes;
    private final int refreshTokenExpirationDays;

    public JwtProperties(String secretKeyString, String accessToken, String refreshToken, int accessTokenExpirationMinutes, int refreshTokenExpirationDays) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;

        secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }
}
