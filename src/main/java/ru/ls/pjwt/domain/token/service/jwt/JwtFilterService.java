package ru.ls.pjwt.domain.token.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.common.property.JwtProperties;

@Service
@RequiredArgsConstructor
public class JwtFilterService {
    private final JwtProperties jwtProperties;
    private final JwtClaimsParser claimsFactory;
    private final JwtValidator jwtValidator;

    // todo check fingerprint (add in future)
    public UserDetails getUserDetails(String jwt) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(jwt);
        jwtValidator.checkType(jwtProperties.getAccessToken(), jwtClaims);
        return jwtClaims.userDetails();
    }
}
