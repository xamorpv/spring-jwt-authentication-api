package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.dto.CreatedRefreshToken;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.properties.ExceptionsProperties;
import ru.ls.pjwt.properties.JwtProperties;
import ru.ls.pjwt.repository.RefreshTokenRepository;
import ru.ls.pjwt.service.jwt.JwtClaimsFactory;
import ru.ls.pjwt.service.jwt.JwtSecurity;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final ExceptionsProperties exceptionsProperties;
    private final JwtClaimsFactory claimsFactory;
    private final JwtSecurity jwtSecurity;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenSecurity refreshTokenSecurity;
    private final RefreshTokenOperator refreshTokenOperator;

    @Transactional
    public void markTokenAsUsed(String token) {
        RefreshToken refreshToken = getToken(claimsFactory.createJwtClaims(token));
        log.debug("try delete token {}", refreshToken);
        refreshTokenOperator.use(refreshToken);
    }

    @Transactional
    public RefreshToken getToken(JwtClaims jwtClaims) {
        jwtSecurity.checkType(jwtProperties.getRefreshToken(), jwtClaims);

        String uuid = jwtClaims.uuid();
        if (uuid == null) {
            log.warn("jwt token without uuid, may be deprecated");
            throw new BadCredentialsException(exceptionsProperties.badCredentials());
        }
        RefreshToken refreshToken = refreshTokenRepository.findByUuid(uuid)
                .orElseThrow(() -> new JwtTokenRequestException("token not found"));

        refreshTokenSecurity.checkUsed(refreshToken);
        return refreshToken;
    }

    @Transactional
    public RefreshToken save(CreatedRefreshToken createdRefreshToken, User user) {
        return refreshTokenRepository.save(new RefreshToken(createdRefreshToken.uuid(), user));
    }
}
