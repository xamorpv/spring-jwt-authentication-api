package ru.ls.pjwt.domain.token.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.id.UUIDGenerator;
import ru.ls.pjwt.common.property.ApplicationProperties;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.common.time.TimeProvider;
import ru.ls.pjwt.domain.token.dto.CreatedRefreshToken;
import ru.ls.pjwt.domain.token.dto.JwtClaims;

/**
 * Factory for creating and rotating JWT access and refresh tokens.
 *
 * <p>This class encapsulates the low‑level token construction logic using the JJWT library and is
 * responsible for signing tokens, setting expiration times, and embedding standard claims (type,
 * authorities, UUID).
 *
 * <p>Token configuration is taken from {@link JwtProperties}, while {@link TimeProvider} and {@link
 * UUIDGenerator} provide timestamps and unique identifiers respectively.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtFactory {
  private final JwtProperties jwtProperties;
  private final TimeProvider timeProvider;
  private final UUIDGenerator uuidGenerator;
  private final ApplicationProperties applicationProperties;
  private final Clock clock;

  /**
   * Creates a signed access token for the given user.
   *
   * @param userDetails the authenticated user's details (used to extract username and authorities)
   * @return a compact access token string
   */
  public String createAccessToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", jwtProperties.getAccessToken());
    claims.put(
        "authorities",
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    return buildToken(
        userDetails.getUsername(),
        Instant.now(clock)
            .plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES),
        claims);
  }

  /**
   * Creates a new refresh token for the specified username.
   *
   * @param username the subject for which the refresh token is issued
   * @return a {@link CreatedRefreshToken} containing the token UUID and the compact token string
   */
  public CreatedRefreshToken createRefreshToken(String username) {
    String uuid = uuidGenerator.random();
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", jwtProperties.getRefreshToken());
    claims.put("uuid", uuid);
    return new CreatedRefreshToken(
        uuid,
        buildToken(
            username,
            Instant.now(clock).plus(jwtProperties.getRefreshTokenExpirationDays(), ChronoUnit.DAYS),
            claims));
  }

  /**
   * Rotates an existing refresh token, preserving the original expiration time.
   *
   * @param jwtClaims the parsed claims of the current refresh token
   * @return a {@link CreatedRefreshToken} with a new UUID and token, but with the same expiration
   *     as the original token
   */
  public CreatedRefreshToken updateRefreshToken(JwtClaims jwtClaims) {
    Claims oldClaims = jwtClaims.claims();
    Instant time = oldClaims.getExpiration().toInstant();
    String username = oldClaims.getSubject();
    Map<String, Object> newClaims = new HashMap<>();
    String uuid = uuidGenerator.random();
    newClaims.put("type", jwtProperties.getRefreshToken());
    newClaims.put("uuid", uuid);
    return new CreatedRefreshToken(uuid, buildToken(username, time, newClaims));
  }

  private String buildToken(String username, Instant time, Map<String, Object> claims) {
    log.debug(
        "creating token: username={}, time={}, claims={}",
        username,
        timeProvider.getFormatter().format(time),
        claims);
    return Jwts.builder()
        .signWith(jwtProperties.getSecretKey())
        .issuer(applicationProperties.name())
        .subject(username)
        .expiration(Date.from(time))
        .issuedAt(Date.from(Instant.now(clock)))
        .claims(claims)
        .compact();
  }
}
