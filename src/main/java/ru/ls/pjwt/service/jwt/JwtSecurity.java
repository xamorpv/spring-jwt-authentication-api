package ru.ls.pjwt.service.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.exception.exceptions.ServerError;

@Service
@Slf4j
public class JwtSecurity {
    public void checkType(String type, JwtClaims claims) {
        String tokenType = claims.claims().get("type", String.class);
        if (tokenType == null) {
            // токен не подделать, значит это какая-то ошибка разработчиков, т.е server error
            throw new ServerError("The token does not contain the 'type' claim");
        }
        if (!tokenType.equals(type)) {
            log.debug("wrong token type {}, expected: {}", tokenType, type);
            throw new JwtTokenRequestException("for this operation expected type was " + type);
        }
    }
}
