package ru.ls.pjwt.service.refresh;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.service.jwt.JwtFactory;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenFactory {
    private final JwtFactory jwtFactory;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenOperator refreshTokenOperator;

    public String createAndSaveToken(String username) {
        String token = jwtFactory.createRefreshToken(username);
        RefreshToken refreshToken = refreshTokenService.save(token);
        log.debug("refresh token saved for user {}, token: {}", username, refreshToken);
        return token;
    }

    public String updateRefreshToken(String token) {
        // todo grace period
        // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а вернуть тот, что был выдан меньше 30 секунд назад)

        log.debug("updating token");
        refreshTokenOperator.use(refreshTokenService.getToken(token));
        String newToken = jwtFactory.updateRefreshToken(token);
        RefreshToken refreshToken = refreshTokenService.save(newToken);
        log.debug("refresh token updated: {}", refreshToken);
        return newToken;
    }

    public void invalidateRefreshToken(String token) {
        refreshTokenService.deleteToken(token);
    }
}
