package ru.ls.pjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.model.JwtClaims;

@RequiredArgsConstructor
@Service
public class JwtClaimsFactory {
    private final JwtParser jwtParser;

    public JwtClaims createJwtClaims(String token) {
        return new JwtClaims(token, jwtParser);
    }
}
