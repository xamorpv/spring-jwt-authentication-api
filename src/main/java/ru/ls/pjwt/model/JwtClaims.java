package ru.ls.pjwt.model;

import io.jsonwebtoken.Claims;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ls.pjwt.service.jwt.JwtParser;
import ru.ls.pjwt.utils.JwtUtils;

@ToString
@Slf4j
public class JwtClaims {
    private final JwtParser jwtParser;

    private final Claims claims;

    private UserDetails userDetails;
    private String uuid;

    public JwtClaims(String token, JwtParser jwtParser) {
        this.jwtParser = jwtParser;
        claims = JwtUtils.getClaims(token);
    }

    public void checkType(String type) {
        jwtParser.checkType(type, claims);
    }

    public String getUuid() {
        if (uuid == null) {
            uuid = jwtParser.getUuid(claims);
        }

        return uuid;
    }

    public String getUsername() {
        return getUserDetails().getUsername();
    }

    public UserDetails getUserDetails() {
        if (userDetails == null) {
            return userDetails = jwtParser.extractUserDetails(claims);
        }

        return userDetails;
    }
}
