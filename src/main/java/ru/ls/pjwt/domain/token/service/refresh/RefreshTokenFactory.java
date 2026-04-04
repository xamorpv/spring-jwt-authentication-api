package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.token.dto.CreatedRefreshToken;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.token.service.jwt.JwtFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenFactory {
    private final JwtFactory jwtFactory;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenManager refreshTokenManager;

    public String createAndSaveToken(User user) {
        log.debug("saving token for user: {}", user.getUsername());
        CreatedRefreshToken createdRefreshToken = jwtFactory.createRefreshToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.save(createdRefreshToken, user);
        log.debug("refresh token saved for username {}, token: {}", user.getUsername(), refreshToken.getUuid());
        return createdRefreshToken.token();
    }

    @Transactional
    public String rotateRefreshToken(JwtClaims token, User user) {
        // todo grace period
        // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а вернуть тот, что был выдан меньше 30 секунд назад)

        log.debug("updating token with uuid={} for username={}", token.uuid(), user.getUsername());
        refreshTokenManager.use(refreshTokenService.getToken(token));
        CreatedRefreshToken newToken = jwtFactory.updateRefreshToken(token);
        RefreshToken refreshToken = refreshTokenService.save(newToken, user);
        log.debug("refresh token updated: {}", refreshToken.getUuid());
        return newToken.token();
    }
}
