package ru.ls.pjwt.domain.token.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.token.dto.CreatedRefreshToken;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.common.property.ApplicationProperties;
import ru.ls.pjwt.common.time.TimeProvider;
import ru.ls.pjwt.common.id.UUIDGenerator;
import ru.ls.pjwt.common.property.JwtProperties;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtFactory {
    private final JwtProperties jwtProperties;
    private final TimeProvider timeProvider;
    private final UUIDGenerator uuidGenerator;
    private final ApplicationProperties applicationProperties;
    private final Clock clock;

    public String createAccessToken(UserDetails userDetails) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", jwtProperties.getAccessToken());
        claims.put("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return buildToken(userDetails.getUsername(),
                Instant.now(clock).plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES), claims);
    }

    public CreatedRefreshToken createRefreshToken(String username) {
        String uuid = uuidGenerator.random();
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", jwtProperties.getRefreshToken());
        claims.put("uuid", uuid);
        return new CreatedRefreshToken(uuid, buildToken(username,
                Instant.now(clock).plus(jwtProperties.getRefreshTokenExpirationDays(), ChronoUnit.DAYS), claims));
    }

    public CreatedRefreshToken updateRefreshToken(JwtClaims jwtClaims) {
        Claims oldClaims = jwtClaims.claims();
        Instant time = oldClaims.getExpiration().toInstant();
        String username = oldClaims.getSubject();
        HashMap<String, Object> newClaims = new HashMap<>();
        String uuid = uuidGenerator.random();
        newClaims.put("type", jwtProperties.getRefreshToken());
        newClaims.put("uuid", uuid);
        return new CreatedRefreshToken(uuid, buildToken(username, time, newClaims));
    }

    private String buildToken(String username, Instant time, HashMap<String, Object> claims) {
        log.debug("creating token: username={}, time={}, claims={}", username, timeProvider.getFormatter().format(time), claims);
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
