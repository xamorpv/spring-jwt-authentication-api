package ru.ls.pjwt.service.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.model.JwtClaims;

@Service
@Slf4j
public class JwtSecurity {
    public void checkType(String type, JwtClaims claims) {
        String tokenType = claims.getClaims().get("type", String.class);
        if (!tokenType.equals(type)) {
            log.debug("wrong token type {}, expected: {}", tokenType, type);
            throw new JwtTokenRequestException("for this operation expected type was "+type);
        }
    }
}
