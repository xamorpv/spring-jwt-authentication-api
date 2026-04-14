package ru.ls.pjwt.domain.token.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;

@Slf4j
public class RefreshTokenCompromisingIntegrationTest extends WebSecurityTest {
    @DisplayName("Token compromising flow")
    @Test
    void testTokenCompromisingFlow() throws Exception {
        // сторонние пользователи
        RegisterRequest user1 = new RegisterRequest("test1", "test1@example.com", "12345677890");
        RegisterRequest user2 = new RegisterRequest("test2", "test2@example.com", "12345677890");

        LoginRequest user1LoginRequest = new LoginRequest(user1.username(), user1.rawPassword());
        LoginRequest user2LoginRequest = new LoginRequest(user2.username(), user2.rawPassword());

        userRegistrationHelper.assertSuccessRegistration(user1);
        userRegistrationHelper.assertSuccessRegistration(user2);

        LoginResponse user1Login1 = userAuthenticationHelper.login(user1LoginRequest);
        LoginResponse user1Login2 = userAuthenticationHelper.login(user1LoginRequest);
        LoginResponse user1Login3 = userAuthenticationHelper.login(user1LoginRequest);

        StandardResponse<LoginResponse> user1Refresh = refreshTokenHelper.assertSuccessRefresh(user1Login1.refreshToken());
        StandardResponse<LoginResponse> user1RefreshChain = refreshTokenHelper.assertSuccessRefresh(user1Refresh.data().refreshToken());


        LoginResponse user2Login = userAuthenticationHelper.login(user2LoginRequest);

        userRegistrationHelper.assertSuccessRegistration();
        LoginResponse loginResponse1 = userAuthenticationHelper.login();
        LoginResponse loginResponse2 = userAuthenticationHelper.login();
        LoginResponse loginResponse3 = userAuthenticationHelper.login();

        StandardResponse<LoginResponse> refreshLoginSuccess = refreshTokenHelper.assertSuccessRefresh(loginResponse1.refreshToken()); // успех
        refreshTokenHelper.assertFailureRefresh(loginResponse1.refreshToken()); // компроментация всех токенов пользователя: loginResponse1, loginResponse2, loginResponse3, refreshLoginSuccess
        refreshTokenHelper.assertFailureRefresh(loginResponse2.refreshToken()); // неудача, но по сути ничего не происходит

        LoginResponse loginResponseSuccess = userAuthenticationHelper.login(); // новый вход легитимного пользователя

        refreshTokenHelper.assertFailureRefresh(loginResponse1.refreshToken()); // злоумышленник пытается сбросить сессию

        StandardResponse<LoginResponse> refreshLoginSuccessAfterCompromising = refreshTokenHelper.assertSuccessRefresh(loginResponseSuccess.refreshToken()); // успех - компроментированный токен ни на что не влияет
        // попытки использовать другие токены. везде неудача
        refreshTokenHelper.assertFailureRefresh(loginResponse3.refreshToken());
        refreshTokenHelper.assertFailureRefresh(refreshLoginSuccess.data().refreshToken());
        // пользователь может продолжать цепочку обновлений
        refreshTokenHelper.assertSuccessRefresh(refreshLoginSuccessAfterCompromising.data().refreshToken());

        // другие пользователи могут продолжать цепочку обновлений
        refreshTokenHelper.assertSuccessRefresh(user2Login.refreshToken());
        refreshTokenHelper.assertSuccessRefresh(user1Login2.refreshToken());
        refreshTokenHelper.assertSuccessRefresh(user1Login3.refreshToken());
        refreshTokenHelper.assertSuccessRefresh(user1RefreshChain.data().refreshToken());
    }


}