package ru.ls.pjwt.refresh.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ls.pjwt.dto.api.response.LoginResponse;
import ru.ls.pjwt.helper.IntegrationRequestHelper;
import ru.ls.pjwt.helper.ThreadHelper;
import ru.ls.pjwt.service.jwt.JwtClaimsFactory;
import ru.ls.pjwt.service.refresh.RefreshTokenSecurity;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CompromisedTokenCoveringIntegrationTest {
    @Autowired
    private IntegrationRequestHelper integrationRequestHelper;

    @ServiceConnection
    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


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
    private RefreshTokenSecurity refreshTokenSecurity;

    @Autowired
    private JwtClaimsFactory claimsFactory;

    @Autowired
    private ThreadHelper threadHelper;

    @Test
    void test() throws Exception {
        integrationRequestHelper.assertSuccessRegistration();
        LoginResponse refreshTokenA = integrationRequestHelper.login();// 1. пользователь получает токен А
        LoginResponse refreshTokenB = integrationRequestHelper.assertSuccessRefresh(refreshTokenA).data();// 2. пользователь обновляет токен А, получает токен Б

        doAnswer(invocation -> {
            // токен B уже загружен из бд, компроментируем токены
            threadHelper.runInIndependentThread(() -> integrationRequestHelper.assertFailureRefresh(refreshTokenA));
            return invocation.callRealMethod();
        }).when(refreshTokenSecurity).checkUsed(argThat(r ->
                r.getUuid().equals(claimsFactory.createJwtClaims(refreshTokenB.refreshToken()).uuid())));

        integrationRequestHelper.assertFailureRefresh(refreshTokenB); // затирание токена в doAnswer должно быть обнаружено через версию
    }
}
