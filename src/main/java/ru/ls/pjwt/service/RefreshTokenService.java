package ru.ls.pjwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.exception.exceptions.RefreshTokenCompromiseException;
import ru.ls.pjwt.model.JwtToken;
import ru.ls.pjwt.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final Argon2PasswordEncoder passwordEncoder;
    private final UserService userService;

    public String createAndSaveToken(String username) {
        String token = jwtService.createRefreshToken(username);
        log.debug("refresh token saved for user {}, token: {}", username, save(token));
        return token;
    }

    public String updateRefreshToken(String token) {
        // todo grace period
        // если прошло меньше 30 секунд, то делаем вид, что этот токен работает (не создавать новый, а вернуть тот, что был выдан меньше 30 секунд назад)

        log.debug("updating token {}", token);
        use(getToken(token));
        String newToken = jwtService.updateRefreshToken(token);
        log.debug("refresh token updated; before: {}, after: {}", token, save(newToken));
        return newToken;
    }

    public void deleteToken(String token) {
        log.debug("try delete token {}", token);
        RefreshToken refreshToken = getToken(token);
        use(refreshToken);
    }

    private RefreshToken getToken(String token) {
        // находим точное совпадение токена в бд (для начала ищем по username, чтобы не перебирать все токены
        RefreshToken refreshToken = refreshTokenRepository.findByUsername(new JwtToken(token).getUsername())
                .stream().filter(t -> passwordEncoder.matches(token, t.getToken()))
                .findFirst()
                .orElseThrow(()->new JwtTokenRequestException("token not found"));
        checkUsed(refreshToken);
        return refreshToken;
    }

    private void checkUsed(RefreshToken refreshToken) {
        if (refreshToken.getUsed()) {
            log.warn("token already used: {}", refreshToken);
            refreshTokenRepository.findActiveByUsername(refreshToken.getUser().getUsername()).forEach(this::use);
            throw new RefreshTokenCompromiseException();
        }
    }

    private void use(RefreshToken refreshToken) {
        log.debug("using token {}", refreshToken);
        refreshToken.setUsed(true);
        refreshToken.setUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }

    private String save(String token) {
        String encoded = passwordEncoder.encode(token);
        refreshTokenRepository.save(new RefreshToken(encoded, userService.loadUser(new JwtToken(token).getUsername())));
        return encoded;
    }


    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    private void clearRefreshTokens() {
        refreshTokenRepository.deleteUsedLater(Instant.now().plus(30, ChronoUnit.DAYS));
    }
}
