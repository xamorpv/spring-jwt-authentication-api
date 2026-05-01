package ru.ls.pjwt.domain.token.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.token.service.jwt.JwtParser;
import ru.ls.pjwt.domain.token.service.refresh.RefreshTokenValidator;
import ru.ls.pjwt.execution.ConcurrentExecutor;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Slf4j
@Import({RegistrationSteps.class, AuthenticationSteps.class, RefreshTokenSteps.class})
class CompromisedTokenCoveringIntegrationTest extends WebIntegrationTest {
  // кейс:
  // 1. пользователь получает токен А
  // 2. пользователь обновляет токен А, получает токен Б
  // 3. два параллельных события:
  // 3.1 злоумышленник пытается использовать токен А
  // 3.2 пользователь обновляет токен Б
  // 4. сервер делает useAndCompromiseTokensForUser в ответ на запрос от злоумышленника, и помечает
  // все токены compromised & used
  // 5. сервер обновляет токен Б, делая его used = true, но compromised заменяет на false (в 3.2 он
  // был false, но в 4 стал true, что не было обнаружено)
  // 6. итог: токен Б остался compromised = false, хотя должен быть compromised = true. из-за этого,
  // если злоумышленник его получил, он сможет один раз сбросить все активные токены пользователя

  @MockitoSpyBean private RefreshTokenValidator refreshTokenValidator;

  @Autowired private RegistrationSteps registrationSteps;

  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private RefreshTokenSteps refreshTokenSteps;

  @Autowired private JwtParser jwtParser;

  @Autowired private ConcurrentExecutor concurrentExecutor;

  @Test
  @DisplayName("Token should remain compromised after concurrent refresh and reuse attempt")
  void givenRefreshToken_whenConcurrentRefreshAndReuse_thenTokenRemainsCompromised()
      throws Exception {
    registrationSteps.registerSuccessfully();
    final LoginResponse refreshTokenA =
        authenticationSteps.loginAsFixtureUser(); // 1. пользователь получает токен А
    final LoginResponse refreshTokenB =
        refreshTokenSteps.refreshTokensSuccessfully(
            refreshTokenA.refreshToken()); // 2. пользователь обновляет токен А, получает токен Б

    doAnswer(
            invocation -> {
              // токен B уже загружен из бд, компроментируем токены
              concurrentExecutor.runAsyncAndWait(
                  () -> refreshTokenSteps.expectTokenCompromised(refreshTokenA.refreshToken()));
              return invocation.callRealMethod();
            })
        .when(refreshTokenValidator)
        .checkUsed(
            argThat(
                r ->
                    r.getUuid()
                        .equals(jwtParser.parseRefreshToken(refreshTokenB.refreshToken()).uuid())));

    refreshTokenSteps.expectTokenCompromised(
        refreshTokenB
            .refreshToken()); // затирание токена в doAnswer должно быть обнаружено через версию
  }
}
