package ru.ls.pjwt.common.property;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.jwt")
@Getter
public class JwtProperties {
  private final SecretKey secretKey;
  private final String accessToken;
  private final String refreshToken;
  private final int accessTokenExpirationMinutes;
  private final int refreshTokenExpirationDays;

  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public JwtProperties(
      final String secretKeyString,
      final String accessToken,
      final String refreshToken,
      final int accessTokenExpirationMinutes,
      final int refreshTokenExpirationDays) {
    secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    this.refreshTokenExpirationDays = refreshTokenExpirationDays;
  }
}
