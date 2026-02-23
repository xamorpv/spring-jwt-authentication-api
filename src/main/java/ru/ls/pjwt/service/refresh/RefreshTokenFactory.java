package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.service.jwt.JwtFactory;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenFactory {
    private final JwtFactory jwtFactory;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenOperator refreshTokenOperator;

    public String createAndSaveToken(User user) {
        String token = jwtFactory.createRefreshToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.save(token, user);
        log.debug("refresh token saved for username {}, token: {}", user.getUsername(), refreshToken);
        return token;
    }

    public String rotateRefreshToken(JwtClaims token) {
        // todo grace period
        // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а вернуть тот, что был выдан меньше 30 секунд назад)

        log.debug("updating token");
        refreshTokenOperator.use(refreshTokenService.getToken(token));
        String newToken = jwtFactory.updateRefreshToken(token);
        RefreshToken refreshToken = refreshTokenService.save(newToken, null);
        log.debug("refresh token updated: {}", refreshToken);
        return newToken;
    }

    public void invalidateRefreshToken(String token) {
        refreshTokenService.markTokenAsUsed(token);
    }
}
