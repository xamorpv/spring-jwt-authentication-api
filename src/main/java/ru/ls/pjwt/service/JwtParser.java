package ru.ls.pjwt.service;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.db.UserDetailsImpl;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;

import java.util.List;

@Slf4j
@Service
public class JwtParser {
    public void checkType(String type, Claims claims) {
        String tokenType = claims.get("type", String.class);
        if (!tokenType.equals(type)) {
            log.debug("wrong token type {}, expected: {}", tokenType, type);
            throw new JwtTokenRequestException("for this operation expected type was "+type);
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

        return new UserDetailsImpl(username, authorities);
    }

    public String getUuid(Claims claims) {
        return claims.get("uuid", String.class);
    }
}
