package ru.ls.pjwt.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.CreatedRefreshToken;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.utils.TimeUtils;
import ru.ls.pjwt.utils.UUIDUtils;
import ru.ls.pjwt.utils.constants.App;
import ru.ls.pjwt.utils.constants.Jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtFactory {
    public String createAccessToken(UserDetails userDetails) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", Jwt.ACCESS);
        claims.put("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return buildToken(userDetails.getUsername(),
                Instant.now().plus(Jwt.accessTokenExpirationMinutes, ChronoUnit.MINUTES), claims);
    }

    public CreatedRefreshToken createRefreshToken(String username) {
        String uuid = UUIDUtils.random();
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", Jwt.REFRESH);
        claims.put("uuid", uuid);
        return new CreatedRefreshToken(uuid, buildToken(username,
                Instant.now().plus(Jwt.refreshTokenExpirationDays, ChronoUnit.DAYS), claims));
    }

    public CreatedRefreshToken updateRefreshToken(JwtClaims jwtClaims) {
        Claims oldClaims = jwtClaims.claims();
        Instant time = oldClaims.getExpiration().toInstant();
        String username = oldClaims.getSubject();
        HashMap<String, Object> newClaims = new HashMap<>();
        String uuid = UUIDUtils.random();
        newClaims.put("type", Jwt.REFRESH);
        newClaims.put("uuid", uuid);
        return new CreatedRefreshToken(uuid, buildToken(username, time, newClaims));
    }

    private String buildToken(String username, Instant time, HashMap<String, Object> claims) {
        log.debug("creating token: username={}, time={}, claims={}", username, TimeUtils.formatter.format(time), claims);
        return Jwts.builder()
                .signWith(Jwt.secretKey)
                .issuer(App.name)
                .subject(username)
                .expiration(Date.from(time))
                .issuedAt(Date.from(Instant.now()))
                .claims(claims)
                .compact();
    }
}
