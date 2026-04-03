package ru.ls.pjwt.domain.token.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.common.properties.ApplicationProperties;
import ru.ls.pjwt.common.properties.JwtProperties;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtParser {
    private final JwtProperties jwtProperties;
    private final ApplicationProperties applicationProperties;

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKey()) // подпись и expiration time уже проверены. username нужно проверить на null, а его наличие уже проверено
                    .requireIssuer(applicationProperties.name())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("expired jwt exception {}", e.getMessage(), e);
            throw new JwtTokenRequestException("jwt token expired");
        } catch (JwtException e) {
            log.debug("jwt exception {}", e.getMessage(), e);
            throw new JwtTokenRequestException("incorrect jwt");
        }
    }

    public UserDetails extractUserDetails(Claims claims) {
        log.debug("extract userDetails for claims {}", claims);
        String username = claims.getSubject();
        if (username == null) {
            log.warn("username not found");
            throw new JwtTokenRequestException("incorrect jwt"); // не раскрываем информацию - sub может отсутствовать только если пользователь пытался изменить токен
        }

        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);

        log.debug("userDetails extracted successfully: username={}, authorities: {}", username, authorities);
        return new User(username, "", authorities == null ?
                new HashSet<>() : authorities.stream().map(SimpleGrantedAuthority::new).toList());
    }

    public String getUuid(Claims claims) {
        return claims.get("uuid", String.class);
    }
}
