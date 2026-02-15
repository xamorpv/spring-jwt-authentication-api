package ru.ls.pjwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.repository.RefreshTokenRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final Argon2PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtClaimsFactory claimsFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenSecurity refreshTokenSecurity;

    public void deleteToken(String token) {
        RefreshToken refreshToken = getToken(token);
        log.debug("try delete token {}", refreshToken);
        refreshTokenSecurity.use(refreshToken);
    }

    public RefreshToken getToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByUuid(claimsFactory.createJwtClaims(token).getUuid())
                .orElseThrow(()->new JwtTokenRequestException("token not found"));
        refreshTokenSecurity.checkUsed(refreshToken);
        return refreshToken;
    }

    public RefreshToken save(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        return refreshTokenRepository.save(new RefreshToken(passwordEncoder.encode(token), jwtClaims.getUuid(), userService.loadUser(jwtClaims.getUsername())));
    }
}
