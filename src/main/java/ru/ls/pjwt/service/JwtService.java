package ru.ls.pjwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.repository.UserRepository;
import ru.ls.pjwt.utils.JwtUtils;
import ru.ls.pjwt.utils.TimeUtils;
import ru.ls.pjwt.utils.UUIDUtils;
import ru.ls.pjwt.utils.constants.App;
import ru.ls.pjwt.utils.constants.Jwt;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;

    public String createAccessToken(String username) {
        return createToken(username, Jwt.ACCESS);
    }

    public String createRefreshToken(String username) {
        return createToken(username, Jwt.REFRESH);
    }

    public String updateRefreshToken(String token) {
        Claims claims = JwtUtils.getClaims(token);
        Instant time = Instant.ofEpochSecond(claims.get("exp", Long.class));
        String username = claims.getSubject();
        HashMap<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("type", Jwt.REFRESH);
        return buildToken(username, time, claimsMap);
    }

    private String createToken(String username, String type) {
        boolean isAccess = type.equals(Jwt.ACCESS);
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        if (isAccess) {
            claims.put("authorities", userRepository.findRolesByUsername(username).stream().map(Authority::getAuthority).toList());
        } else {
            claims.put("uuid", UUIDUtils.random());
        }
        Instant time = isAccess?Instant.now().plus(Jwt.accessTokenExpirationMinutes, ChronoUnit.MINUTES) :
                Instant.now().plus(Jwt.refreshTokenExpirationDays, ChronoUnit.DAYS);
        return buildToken(username, time, claims);
    }

    private String buildToken(String username, Instant time, HashMap<String, Object> claims) {
        log.info("creating token: username={}, time={}, claims={}", username, TimeUtils.formatter.format(time), claims);
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
