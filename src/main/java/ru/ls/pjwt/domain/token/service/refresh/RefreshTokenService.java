package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.domain.token.dto.CreatedRefreshToken;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;
import ru.ls.pjwt.domain.token.service.jwt.JwtValidator;
import ru.ls.pjwt.domain.user.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final ExceptionsProperties exceptionsProperties;
    private final JwtValidator jwtValidator;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenValidator refreshTokenValidator;
    private final RefreshTokenManager refreshTokenManager;

    @Transactional
    public void markTokenAsUsed(JwtClaims claims) {
        RefreshToken refreshToken = getToken(claims);
        log.debug("try delete token {}", refreshToken.getUuid());
        refreshTokenManager.use(refreshToken);
    }

    @Transactional
    public RefreshToken getToken(JwtClaims jwtClaims) {
        jwtValidator.validateType(jwtProperties.getRefreshToken(), jwtClaims);

        String uuid = jwtClaims.uuid();
        if (uuid == null) {
            log.warn("jwt token without uuid, may be deprecated");
            throw new BadCredentialsException(exceptionsProperties.badCredentials());
        }
        RefreshToken refreshToken = refreshTokenRepository.findByUuid(uuid)
                .orElseThrow(() -> new JwtTokenRequestException("token not found"));

        refreshTokenValidator.checkUsed(refreshToken);
        return refreshToken;
    }

    @Transactional
    public RefreshToken save(CreatedRefreshToken createdRefreshToken, User user) {
        return refreshTokenRepository.save(new RefreshToken(createdRefreshToken.uuid(), user));
    }
}
