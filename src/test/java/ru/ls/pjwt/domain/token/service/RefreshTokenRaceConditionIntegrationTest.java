package ru.ls.pjwt.domain.token.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                // тест должен падать в случае дедлока
                "spring.datasource.hikari.data-source-properties.options=-c lock_timeout=3000 -c statement_timeout=5000 -c idle_in_transaction_session_timeout=10000"
        }
)
@Import({RegistrationSteps.class, AuthenticationSteps.class, RefreshTokenSteps.class})
public class RefreshTokenRaceConditionIntegrationTest extends WebIntegrationTest {
    @Autowired
    private RegistrationSteps registrationSteps;

    @Autowired
    private AuthenticationSteps authenticationSteps;

    @Autowired
    private RefreshTokenSteps refreshTokenSteps;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvcClient mockMvcClient;

    @DisplayName("Refresh token race condition: token replay")
    @Test
    void givenRefreshToken_whenConcurrentRotation_thenCompromiseAllTokens() throws Exception {
        registrationSteps.registerSuccessfully();
        LoginResponse loginResponse = authenticationSteps.loginAsFixtureUser();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Callable<MockHttpServletResponse> refreshRequest = () -> {
            countDownLatch.await();
            return mockMvcClient.postReturningStatus("/api/v1/auth/refresh", loginResponse);
        };

        MockHttpServletResponse result1;
        MockHttpServletResponse result2;

        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            Future<MockHttpServletResponse> future1 = executorService.submit(refreshRequest);
            Future<MockHttpServletResponse> future2 = executorService.submit(refreshRequest);

            countDownLatch.countDown();

            result1 = future1.get(10, TimeUnit.SECONDS);
            result2 = future2.get(10, TimeUnit.SECONDS);

            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }

        log.info("result 1: {}", result1);
        log.info("result 2: {}", result2);

        boolean success1 = result1.getStatus() == 200;
        boolean success2 = result2.getStatus() == 200;

        assertNotEquals(success1, success2, "First token should be rotated successfully, second token should be compromised");

        String tokenBody;

        if (success1) {
            tokenBody = result1.getContentAsString();
        } else {
            tokenBody = result2.getContentAsString();
        }

        StandardResponse<LoginResponse> refreshResponse = objectMapper.readValue(tokenBody, new TypeReference<>() {});
        refreshTokenSteps.expectTokenCompromised(refreshResponse.data().refreshToken());
    }
}
