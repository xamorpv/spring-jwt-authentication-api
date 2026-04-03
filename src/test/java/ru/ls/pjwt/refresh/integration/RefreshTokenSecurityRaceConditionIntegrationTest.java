package ru.ls.pjwt.refresh.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ls.pjwt.common.web.api.dto.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.helper.IntegrationRequestHelper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                // тест должен падать в случае дедлока
                "spring.datasource.hikari.data-source-properties.options=-c lock_timeout=3000 -c statement_timeout=5000 -c idle_in_transaction_session_timeout=10000",
                "spring.datasource.hikari.connection-timeout=3000"
        }
)
public class RefreshTokenSecurityRaceConditionIntegrationTest {

    @ServiceConnection
    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private IntegrationRequestHelper integrationRequestHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test() throws Exception {
        integrationRequestHelper.assertSuccessRegistration();
        LoginResponse loginResponse = integrationRequestHelper.login();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Callable<String> refreshRequest = () -> {
            countDownLatch.await();
            return integrationRequestHelper.refreshStringBody(loginResponse);
        };

        String result1;
        String result2;

        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            Future<String> future1 = executorService.submit(refreshRequest);
            Future<String> future2 = executorService.submit(refreshRequest);

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

        boolean success1 = parseSuccess(result1);
        boolean success2 = parseSuccess(result2);

        assertNotEquals(success1, success2, "results should not be equals");

        String tokenBody;

        if (success1) {
            tokenBody = result1;
        } else {
            tokenBody = result2;
        }

        StandardResponse<LoginResponse> refreshResponse = objectMapper.readValue(tokenBody, new TypeReference<>() {});
        integrationRequestHelper.assertFailureRefresh(refreshResponse.data());
    }

    private boolean parseSuccess(String result) {
        JsonNode treeNode = objectMapper.readTree(result);
        boolean success = treeNode.at("/success").asBoolean();
        if (!success) {
            assertEquals(HttpStatus.UNAUTHORIZED.value(), treeNode.at("/data/statusCode").asInt());
        }
        return success;
    }
}
