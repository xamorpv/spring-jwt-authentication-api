package ru.ls.pjwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtFactory jwtFactory;
    private final Argon2PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PlatformTransactionManager transactionManager;
    private final JwtClaimsFactory claimsFactory;

    private TransactionTemplate getNewTransactionTemplate() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.setTimeout(30);
        return template;
    }

    public String createAndSaveToken(String username) {
        String token = jwtFactory.createRefreshToken(username);
        RefreshToken refreshToken = save(token);
        log.debug("refresh token saved for user {}, token: {}", username, refreshToken);
        return token;
    }

    public String updateRefreshToken(String token) {
        // todo grace period
        // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а вернуть тот, что был выдан меньше 30 секунд назад)

        log.debug("updating token {}", token);
        use(getToken(token));
        String newToken = jwtFactory.updateRefreshToken(token);
        RefreshToken refreshToken = save(newToken);
        log.debug("refresh token updated: {}", refreshToken);
        return newToken;
    }

    public void deleteToken(String token) {
        RefreshToken refreshToken = getToken(token);
        log.debug("try delete token {}", refreshToken);
        use(refreshToken);
    }

    private RefreshToken getToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByUuid(claimsFactory.createJwtClaims(token).getUuid())
                .orElseThrow(()->new JwtTokenRequestException("token not found"));
        checkUsed(refreshToken);
        return refreshToken;
    }

    private void checkUsed(RefreshToken refreshToken) {
        if (refreshToken.getUsed()) {
            log.warn("token already used; using all tokens for this user");
            getNewTransactionTemplate().execute(status -> {
                refreshTokenRepository.findActiveByUsername(refreshToken.getUser().getUsername()).forEach(this::use);
                return null;
            });
            throw new JwtTokenRequestException("refresh token was compromised. you may be get hacked. please re-login");
        }
    }

    private void use(RefreshToken refreshToken) {
        log.debug("using token {}", refreshToken);
        refreshToken.setUsed(true);
        refreshToken.setUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken save(String token) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(token);
        return refreshTokenRepository.save(new RefreshToken(passwordEncoder.encode(token), jwtClaims.getUuid(), userService.loadUser(jwtClaims.getUsername())));
    }


    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    private void clearRefreshTokens() {
        log.debug("clearing tokens");
        refreshTokenRepository.deleteUsedLater(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
