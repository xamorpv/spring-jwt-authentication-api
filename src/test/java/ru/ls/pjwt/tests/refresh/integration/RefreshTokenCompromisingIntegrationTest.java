package ru.ls.pjwt.tests.refresh.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;

@Slf4j
public class RefreshTokenCompromisingIntegrationTest extends WebSecurityTest {
    @Test
    void test() throws Exception {
        // сторонние пользователи
        RegisterRequest user1 = new RegisterRequest("test1", "test1@example.com", "12345677890");
        RegisterRequest user2 = new RegisterRequest("test2", "test2@example.com", "12345677890");

        LoginRequest user1LoginRequest = new LoginRequest(user1.username(), user1.rawPassword());
        LoginRequest user2LoginRequest = new LoginRequest(user2.username(), user2.rawPassword());

        refreshTokenHelper.assertSuccessRegistration(user1);
        refreshTokenHelper.assertSuccessRegistration(user2);

        LoginResponse user1Login1 = refreshTokenHelper.login(user1LoginRequest);
        LoginResponse user1Login2 = refreshTokenHelper.login(user1LoginRequest);
        LoginResponse user1Login3 = refreshTokenHelper.login(user1LoginRequest);

        StandardResponse<LoginResponse> user1Refresh = refreshTokenHelper.assertSuccessRefresh(user1Login1);
        StandardResponse<LoginResponse> user1RefreshChain = refreshTokenHelper.assertSuccessRefresh(user1Refresh.data());


        LoginResponse user2Login = refreshTokenHelper.login(user2LoginRequest);

        refreshTokenHelper.assertSuccessRegistration();
        LoginResponse loginResponse1 = refreshTokenHelper.login();
        LoginResponse loginResponse2 = refreshTokenHelper.login();
        LoginResponse loginResponse3 = refreshTokenHelper.login();

        StandardResponse<LoginResponse> refreshLoginSuccess = refreshTokenHelper.assertSuccessRefresh(loginResponse1); // успех
        refreshTokenHelper.assertFailureRefresh(loginResponse1); // компроментация всех токенов пользователя: loginResponse1, loginResponse2, loginResponse3, refreshLoginSuccess
        refreshTokenHelper.assertFailureRefresh(loginResponse2); // неудача, но по сути ничего не происходит

        LoginResponse loginResponseSuccess = refreshTokenHelper.login(); // новый вход легитимного пользователя

        refreshTokenHelper.assertFailureRefresh(loginResponse1); // злоумышленник пытается сбросить сессию

        StandardResponse<LoginResponse> refreshLoginSuccessAfterCompromising = refreshTokenHelper.assertSuccessRefresh(loginResponseSuccess); // успех - компроментированный токен ни на что не влияет
        // попытки использовать другие токены. везде неудача
        refreshTokenHelper.assertFailureRefresh(loginResponse3);
        refreshTokenHelper.assertFailureRefresh(refreshLoginSuccess.data());
        // пользователь может продолжать цепочку обновлений
        refreshTokenHelper.assertSuccessRefresh(refreshLoginSuccessAfterCompromising.data());

        // другие пользователи могут продолжать цепочку обновлений
        refreshTokenHelper.assertSuccessRefresh(user2Login);
        refreshTokenHelper.assertSuccessRefresh(user1Login2);
        refreshTokenHelper.assertSuccessRefresh(user1Login3);
        refreshTokenHelper.assertSuccessRefresh(user1RefreshChain.data());
    }


}