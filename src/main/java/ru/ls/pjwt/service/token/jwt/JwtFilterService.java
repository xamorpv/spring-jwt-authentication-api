package ru.ls.pjwt.service.token.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.tokens.JwtClaims;
import ru.ls.pjwt.properties.JwtProperties;

@Service
@RequiredArgsConstructor
public class JwtFilterService {
    private final JwtProperties jwtProperties;
    private final JwtClaimsFactory claimsFactory;
    private final JwtSecurity jwtSecurity;

    // todo check fingerprint (add in future)
    public UserDetails getUserDetails(String jwt) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(jwt);
        jwtSecurity.checkType(jwtProperties.getAccessToken(), jwtClaims);
        return jwtClaims.userDetails();
    }
}
