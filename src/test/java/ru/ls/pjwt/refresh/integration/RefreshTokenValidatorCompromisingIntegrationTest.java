package ru.ls.pjwt.refresh.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.helper.IntegrationRequestHelper;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RefreshTokenValidatorCompromisingIntegrationTest {

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
        // сторонние пользователи
        RegisterRequest user1 = new RegisterRequest("test1", "test1@example.com", "12345677890");
        RegisterRequest user2 = new RegisterRequest("test2", "test2@example.com", "12345677890");

        LoginRequest user1LoginRequest = new LoginRequest(user1.username(), user1.rawPassword());
        LoginRequest user2LoginRequest = new LoginRequest(user2.username(), user2.rawPassword());

        integrationRequestHelper.assertSuccessRegistration(user1);
        integrationRequestHelper.assertSuccessRegistration(user2);

        LoginResponse user1Login1 = integrationRequestHelper.login(user1LoginRequest);
        LoginResponse user1Login2 = integrationRequestHelper.login(user1LoginRequest);
        LoginResponse user1Login3 = integrationRequestHelper.login(user1LoginRequest);

        StandardResponse<LoginResponse> user1Refresh = integrationRequestHelper.assertSuccessRefresh(user1Login1);
        StandardResponse<LoginResponse> user1RefreshChain = integrationRequestHelper.assertSuccessRefresh(user1Refresh.data());


        LoginResponse user2Login = integrationRequestHelper.login(user2LoginRequest);

        integrationRequestHelper.assertSuccessRegistration();
        LoginResponse loginResponse1 = integrationRequestHelper.login();
        LoginResponse loginResponse2 = integrationRequestHelper.login();
        LoginResponse loginResponse3 = integrationRequestHelper.login();

        StandardResponse<LoginResponse> refreshLoginSuccess = integrationRequestHelper.assertSuccessRefresh(loginResponse1); // успех
        integrationRequestHelper.assertFailureRefresh(loginResponse1); // компроментация всех токенов пользователя: loginResponse1, loginResponse2, loginResponse3, refreshLoginSuccess
        integrationRequestHelper.assertFailureRefresh(loginResponse2); // неудача, но по сути ничего не происходит

        LoginResponse loginResponseSuccess = integrationRequestHelper.login(); // новый вход легитимного пользователя

        integrationRequestHelper.assertFailureRefresh(loginResponse1); // злоумышленник пытается сбросить сессию

        StandardResponse<LoginResponse> refreshLoginSuccessAfterCompromising = integrationRequestHelper.assertSuccessRefresh(loginResponseSuccess); // успех - компроментированный токен ни на что не влияет
        // попытки использовать другие токены. везде неудача
        integrationRequestHelper.assertFailureRefresh(loginResponse3);
        integrationRequestHelper.assertFailureRefresh(refreshLoginSuccess.data());
        // пользователь может продолжать цепочку обновлений
        integrationRequestHelper.assertSuccessRefresh(refreshLoginSuccessAfterCompromising.data());

        // другие пользователи могут продолжать цепочку обновлений
        integrationRequestHelper.assertSuccessRefresh(user2Login);
        integrationRequestHelper.assertSuccessRefresh(user1Login2);
        integrationRequestHelper.assertSuccessRefresh(user1Login3);
        integrationRequestHelper.assertSuccessRefresh(user1RefreshChain.data());
    }


}