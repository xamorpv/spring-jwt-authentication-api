package ru.ls.pjwt.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.service.jwt.JwtFactory;
import ru.ls.pjwt.service.jwt.JwtSecurity;
import ru.ls.pjwt.utils.constants.Jwt;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final JwtFactory jwtFactory;
    private final UserSecurity userSecurity;
    private final JwtSecurity jwtSecurity;

    public String createAccessToken(JwtClaims refreshTokenClaims) {
        jwtSecurity.checkType(Jwt.REFRESH, refreshTokenClaims);
        String username = refreshTokenClaims.username();
        UserDetails userDetails = userSecurity.validateUsername(username);
        return jwtFactory.createAccessToken(userDetails);
    }
}
