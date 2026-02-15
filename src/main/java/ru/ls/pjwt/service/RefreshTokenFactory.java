package ru.ls.pjwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.repository.RefreshTokenRepository;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenFactory {
    private final JwtFactory jwtFactory;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenSecurity refreshTokenSecurity;

    public String createAndSaveToken(String username) {
        String token = jwtFactory.createRefreshToken(username);
        RefreshToken refreshToken = refreshTokenService.save(token);
        log.debug("refresh token saved for user {}, token: {}", username, refreshToken);
        return token;
    }

    public String updateRefreshToken(String token) {
        // todo grace period
        // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а вернуть тот, что был выдан меньше 30 секунд назад)

        log.debug("updating token {}", token);
        refreshTokenSecurity.use(refreshTokenService.getToken(token));
        String newToken = jwtFactory.updateRefreshToken(token);
        RefreshToken refreshToken = refreshTokenService.save(newToken);
        log.debug("refresh token updated: {}", refreshToken);
        return newToken;
    }

    public void invalidateRefreshToken(String token) {
        refreshTokenService.deleteToken(token);
    }
}
