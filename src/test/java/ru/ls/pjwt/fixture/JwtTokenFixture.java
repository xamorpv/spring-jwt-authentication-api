package ru.ls.pjwt.fixture;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.property.ApplicationProperties;
import ru.ls.pjwt.common.property.JwtProperties;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenFixture {
    @Getter
    private JwtBuilder baseJwtTokenBuilder;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private Clock clock;

    @PostConstruct
    private void init() {
        baseJwtTokenBuilder = Jwts.builder()
                .signWith(jwtProperties.getSecretKey())
                .issuer(applicationProperties.name())
                .subject("username")
                .expiration(Date.from(Instant.now(clock).plus(30, ChronoUnit.MINUTES)))
                .issuedAt(Date.from(Instant.now(clock)));
    }
}
