package ru.ls.pjwt.domain.token.service.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.common.web.exception.ServerError;

@Service
@Slf4j
public class JwtValidator {
    public void checkType(String type, JwtClaims claims) {
        log.debug("check token type for user={}, expected type={}", claims.username(), type);
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
