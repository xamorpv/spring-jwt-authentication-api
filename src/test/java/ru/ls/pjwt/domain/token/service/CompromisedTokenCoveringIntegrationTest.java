package ru.ls.pjwt.domain.token.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.token.service.jwt.JwtParser;
import ru.ls.pjwt.domain.token.service.refresh.RefreshTokenValidator;
import ru.ls.pjwt.helper.ThreadHelper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;

@Slf4j
public class CompromisedTokenCoveringIntegrationTest extends WebSecurityTest {
    // кейс:
    // 1. пользователь получает токен А
    // 2. пользователь обновляет токен А, получает токен Б
    // 3. два параллельных события:
    // 3.1 злоумышленник пытается использовать токен А
    // 3.2 пользователь обновляет токен Б
    // 4. сервер делает useAndCompromiseTokensForUser в ответ на запрос от злоумышленника, и помечает все токены compromised & used
    // 5. сервер обновляет токен Б, делая его used = true, но compromised заменяет на false (в 3.2 он был false, но в 4 стал true, что не было обнаружено)
    // 6. итог: токен Б остался compromised = false, хотя должен быть compromised = true. из-за этого, если злоумышленник его получил, он сможет один раз сбросить все активные токены пользователя

    @MockitoSpyBean
    private RefreshTokenValidator refreshTokenValidator;

    @Autowired
    private JwtParser jwtParser;

    @Autowired
    private ThreadHelper threadHelper;

    @Test
    @DisplayName("Token should remain compromised after concurrent refresh and reuse attempt")
    void givenRefreshToken_whenConcurrentRefreshAndReuse_thenTokenRemainsCompromised() throws Exception {
        refreshTokenHelper.assertSuccessRegistration();
        LoginResponse refreshTokenA = refreshTokenHelper.login();// 1. пользователь получает токен А
        LoginResponse refreshTokenB = refreshTokenHelper.assertSuccessRefresh(refreshTokenA).data();// 2. пользователь обновляет токен А, получает токен Б

        doAnswer(invocation -> {
            // токен B уже загружен из бд, компроментируем токены
            threadHelper.runInIndependentThread(() -> refreshTokenHelper.assertFailureRefresh(refreshTokenA));
            return invocation.callRealMethod();
        }).when(refreshTokenValidator).checkUsed(argThat(r ->
                r.getUuid().equals(jwtParser.parseToken(refreshTokenB.refreshToken()).uuid())));

        refreshTokenHelper.assertFailureRefresh(refreshTokenB); // затирание токена в doAnswer должно быть обнаружено через версию
    }
}
