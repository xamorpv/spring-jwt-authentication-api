package ru.ls.pjwt.common.property;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.jwt")
@Getter
public class JwtProperties {
  private final SecretKey secretKey;
  private final String accessToken;
  private final String refreshToken;
  private final int accessTokenExpirationMinutes;
  private final int refreshTokenExpirationDays;

  /** Injection constructor. */
  public JwtProperties(
      String secretKeyString,
      String accessToken,
      String refreshToken,
      int accessTokenExpirationMinutes,
      int refreshTokenExpirationDays) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    this.refreshTokenExpirationDays = refreshTokenExpirationDays;

    secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
  }
}
