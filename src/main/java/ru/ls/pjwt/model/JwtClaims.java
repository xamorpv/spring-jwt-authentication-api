package ru.ls.pjwt.model;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ls.pjwt.service.jwt.JwtParser;
import ru.ls.pjwt.utils.JwtUtils;

@ToString
@Slf4j
public class JwtClaims {
    private final JwtParser jwtParser;

    @Getter
    private final Claims claims;
    @Getter
    private final UserDetails userDetails;
    @Getter
    private final String uuid;
    @Getter
    private final String username;

    public JwtClaims(String token, JwtParser jwtParser) {
        this.jwtParser = jwtParser;
        claims = JwtUtils.getClaims(token);
        uuid = jwtParser.getUuid(claims);
        userDetails = jwtParser.extractUserDetails(claims);
        username = userDetails.getUsername();
    }
}
