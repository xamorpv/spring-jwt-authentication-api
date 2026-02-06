package ru.ls.pjwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.utils.constants.App;
import ru.ls.pjwt.utils.constants.Jwt;

@Slf4j
@UtilityClass
public class JwtUtils {
    public Claims getClaims(String token) {
        try {
            log.debug("get claims: {}", token);
            return Jwts.parser()
                    .verifyWith(Jwt.secretKey) // подпись и expiration time уже проверены. username нужно проверить на null, а его наличие уже проверено
                    .requireIssuer(App.name)
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
}
