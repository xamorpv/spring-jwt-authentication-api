package ru.ls.pjwt.domain.token.service.refresh;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.common.database.entity.TimestampedEntity;
import ru.ls.pjwt.domain.auth.property.DevUsernamesProperties;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.service.UserService;

@Slf4j
public class RefreshTokenClearingIntegrationTest extends WebIntegrationTest {
  @Autowired private RefreshTokenScheduler refreshTokenScheduler;

  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private UserService userService;

  @Autowired private DevUsernamesProperties devUsernamesProperties;

  @Autowired private Clock clock;

  private final Map<String, RefreshToken> existingRefreshTokens = new HashMap<>();
  private final Map<String, RefreshToken> nonExistingRefreshTokens = new HashMap<>();

  @Test
  @Transactional // чтобы сущности обновлялись при save
  void tokenClearingFlow() {
    User user = userService.findUserByUsername(devUsernamesProperties.user());
    User moder = userService.findUserByUsername(devUsernamesProperties.moderator());
    User admin = userService.findUserByUsername(devUsernamesProperties.admin());

    RefreshToken adminRefreshToken = createRefreshToken(admin);
    RefreshToken moderRefreshToken1 = createRefreshToken(moder);
    RefreshToken moderRefreshToken2 = createRefreshToken(moder);
    RefreshToken moderRefreshToken3 = createRefreshToken(moder);
    RefreshToken userRefreshToken1 = createRefreshToken(user);
    RefreshToken userRefreshToken2 = createRefreshToken(user);
    RefreshToken userRefreshToken3 = createRefreshToken(user);
    RefreshToken userRefreshToken4 = createRefreshToken(user);

    putAll(
        adminRefreshToken,
        moderRefreshToken1,
        moderRefreshToken2,
        moderRefreshToken3,
        userRefreshToken1,
        userRefreshToken2,
        userRefreshToken3,
        userRefreshToken4);

    clearAndCheck();
    // повторный вызов ничего не должен менять
    clearAndCheck();

    expireByCreatedAtAndSave(moderRefreshToken3);
    expireByCreatedAtAndSave(userRefreshToken4);

    expireByUsedAtAndSave(userRefreshToken1);
    expireByUsedAtAndSave(moderRefreshToken1);

    // так как usedAt не null, удаление по createdAt не должно произойти
    expireByCreatedAt(adminRefreshToken);
    adminRefreshToken.setUsed(true);
    adminRefreshToken.setUsedAt(Instant.now(clock));
    refreshTokenRepository.saveAndFlush(adminRefreshToken);

    clearAndCheck();
    clearAndCheck();

    existingRefreshTokens
        .values()
        .forEach(
            refreshToken -> {
              log.debug(
                  "expiring token: used={}, usedAt={}, createdAt={}",
                  refreshToken.isUsed(),
                  refreshToken.getUsedAt(),
                  refreshToken.getCreatedAt());
              use(refreshToken);
              refreshTokenRepository.saveAndFlush(refreshToken);
            });

    nonExistingRefreshTokens.putAll(existingRefreshTokens);
    existingRefreshTokens.clear();

    clearAndCheck();
    clearAndCheck();
  }

  private RefreshToken createRefreshToken(User user) {
    return refreshTokenRepository.saveAndFlush(
        new RefreshToken(UUID.randomUUID().toString(), user));
  }

  private void clearAndCheck() {
    refreshTokenScheduler.clearRefreshTokens();
    expectAllTokensExists();
    expectAllTokensNotExists();
  }

  private void expectAllTokensExists() {
    existingRefreshTokens
        .values()
        .forEach(
            t -> {
              log.debug(
                  "expect token exists: used={}, usedAt={}, createdAt={}",
                  t.isUsed(),
                  t.getUsedAt(),
                  t.getCreatedAt());
              assertTrue(
                  refreshTokenRepository.findByUuid(t.getUuid()).isPresent(),
                  "refresh token should exist: " + t);
            });
  }

  private void expectAllTokensNotExists() {
    nonExistingRefreshTokens
        .values()
        .forEach(
            t -> {
              log.debug(
                  "expect token not exists: used={}, usedAt={}, createdAt={}",
                  t.isUsed(),
                  t.getUsedAt(),
                  t.getCreatedAt());
              assertFalse(
                  refreshTokenRepository.findByUuid(t.getUuid()).isPresent(),
                  "refresh token should not exist");
            });
  }

  private void expireByCreatedAtAndSave(RefreshToken refreshToken) {
    expireByCreatedAt(refreshToken);
    setTokenNonExistent(refreshToken);
    refreshTokenRepository.saveAndFlush(refreshToken);
  }

  private void expireByUsedAtAndSave(RefreshToken refreshToken) {
    use(refreshToken);

    setTokenNonExistent(refreshToken);
    refreshTokenRepository.saveAndFlush(refreshToken);
  }

  private void setTokenNonExistent(RefreshToken refreshToken) {
    existingRefreshTokens.remove(refreshToken.getUuid());
    nonExistingRefreshTokens.put(refreshToken.getUuid(), refreshToken);
  }

  private void expireByCreatedAt(RefreshToken refreshToken) {
    Field createdAt = null;
    try {
      createdAt = TimestampedEntity.class.getDeclaredField("createdAt");
    } catch (NoSuchFieldException e) {
      fail("refreshToken don't have 'createdAt' field");
    }
    createdAt.setAccessible(true);
    Instant expiredInstant = createExpiredInstant();
    try {
      createdAt.set(refreshToken, expiredInstant);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    jdbcTemplate.update(
        "update refresh_tokens set created_at = ? where id = ?",
        Timestamp.from(expiredInstant),
        refreshToken.getId());
  }

  private void use(RefreshToken refreshToken) {
    refreshToken.setUsed(true);
    refreshToken.setUsedAt(createExpiredInstant());
  }

  private Instant createExpiredInstant() {
    return Instant.now(clock).minus(44, ChronoUnit.DAYS).minus(1, ChronoUnit.MINUTES);
  }

  private void putAll(RefreshToken... refreshTokens) {
    for (RefreshToken refreshToken : refreshTokens) {
      existingRefreshTokens.put(refreshToken.getUuid(), refreshToken);
    }
  }
}
