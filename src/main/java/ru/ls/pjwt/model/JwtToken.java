package ru.ls.pjwt.model;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ls.pjwt.dto.db.UserDetailsImpl;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.utils.JwtUtils;

import java.util.List;

@Slf4j
public class JwtToken {
    private final Claims claims;
    private final String token;

    private UserDetails userDetails;
    private String username;

    public JwtToken(String token) {
        this.token = token;
        claims = JwtUtils.getClaims(token);
    }

    public void checkType(String type) {
        if (!claims.get("type", String.class).equals(type)) {
            log.debug("wrong token type {}, expected: {}", token, type);
            throw new JwtTokenRequestException("for this operation expected type was "+type);
        }
    }

    public String getUsername() {
        if (username != null) {
            return username;
        }
        UserDetails userDetails = getUserDetails();
        username = userDetails.getUsername();
        return userDetails.getUsername();
    }

    public UserDetails getUserDetails() {
        if (userDetails != null) {
            return userDetails;
        }
        log.debug("extract userDetails for claims {}", claims);
        String username = claims.getSubject();
        if (username == null) {
            log.warn("username not found");
            throw new JwtTokenRequestException("incorrect jwt"); // не раскрываем информацию - sub может отсутствовать только если пользователь пытался изменить токен
        }

        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);

        UserDetails createdUserDetails = new UserDetailsImpl(username, authorities);
        userDetails = createdUserDetails;
        return createdUserDetails;
    }
}
