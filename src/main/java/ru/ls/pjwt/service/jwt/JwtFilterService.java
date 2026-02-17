package ru.ls.pjwt.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.utils.constants.Jwt;

@Service
@RequiredArgsConstructor
public class JwtFilterService {
    private final JwtClaimsFactory claimsFactory;

    // todo check fingerprint (add in future)
    public UserDetails getUserDetails(String jwt) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(jwt);
        jwtClaims.checkType(Jwt.ACCESS);
        return jwtClaims.getUserDetails();
    }
}
