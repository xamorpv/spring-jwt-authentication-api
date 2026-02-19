package ru.ls.pjwt.service.refresh;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.repository.RefreshTokenRepository;
import ru.ls.pjwt.service.auth.UserService;
import ru.ls.pjwt.service.jwt.JwtClaimsFactory;
import ru.ls.pjwt.utils.constants.Exceptions;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final Argon2PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtClaimsFactory claimsFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenSecurity refreshTokenSecurity;
    private final RefreshTokenOperator refreshTokenOperator;

    public void deleteToken(String token) {
        RefreshToken refreshToken = getToken(claimsFactory.createJwtClaims(token));
        log.debug("try delete token {}", refreshToken);
        refreshTokenOperator.use(refreshToken);
    }

    public RefreshToken getToken(JwtClaims jwtClaims) {
        String uuid = jwtClaims.getUuid();
        if (uuid == null) {
            log.warn("jwt token without uuid, may be deprecated");
            throw new BadCredentialsException(Exceptions.BAD_CREDENTIALS);
        }
        RefreshToken refreshToken = refreshTokenRepository.findByUuid(uuid)
                .orElseThrow(()->new JwtTokenRequestException("token not found"));
        if (!passwordEncoder.matches(jwtClaims.getToken(), refreshToken.getToken())) {
            log.warn("user has uuid in jwt token, but token does not matches. token with same uuid: {}", refreshToken);
            throw new BadCredentialsException(Exceptions.BAD_CREDENTIALS);
        }
        refreshTokenSecurity.checkUsed(refreshToken);
        return refreshToken;
    }

    public RefreshToken save(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        return refreshTokenRepository.save(new RefreshToken(passwordEncoder.encode(token), jwtClaims.getUuid(), userService.loadUser(jwtClaims.getUsername())));
    }
}
