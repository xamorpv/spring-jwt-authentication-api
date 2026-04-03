package ru.ls.pjwt.service.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.tokens.JwtClaims;

@RequiredArgsConstructor
@Service
public class JwtClaimsFactory {
    private final JwtParser jwtParser;

    public JwtClaims createJwtClaims(String token) {
        Claims claims = jwtParser.getClaims(token);
        UserDetails userDetails = jwtParser.extractUserDetails(claims);
        return new JwtClaims(claims, userDetails, jwtParser.getUuid(claims), userDetails.getUsername(), token);
    }
}
