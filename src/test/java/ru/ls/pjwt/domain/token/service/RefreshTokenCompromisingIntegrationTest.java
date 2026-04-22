package ru.ls.pjwt.domain.token.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Slf4j
@Import({RegistrationSteps.class, AuthenticationSteps.class, RefreshTokenSteps.class})
public class RefreshTokenCompromisingIntegrationTest extends WebIntegrationTest {
    @Autowired
    private RegistrationSteps registrationSteps;

    @Autowired
    private AuthenticationSteps authenticationSteps;

    @Autowired
    private RefreshTokenSteps refreshTokenSteps;

    @DisplayName("Token compromising flow")
    @Test
    void testTokenCompromisingFlow() throws Exception {
        // сторонние пользователи
        RegisterRequest user1 = new RegisterRequest("test1", "test1@example.com", "12345677890");
        RegisterRequest user2 = new RegisterRequest("test2", "test2@example.com", "12345677890");

        LoginRequest user1LoginRequest = new LoginRequest(user1.username(), user1.rawPassword());
        LoginRequest user2LoginRequest = new LoginRequest(user2.username(), user2.rawPassword());

        registrationSteps.registerSuccessfully(user1);
        registrationSteps.registerSuccessfully(user2);

        LoginResponse user1Login1 = authenticationSteps.login(user1LoginRequest);
        LoginResponse user1Login2 = authenticationSteps.login(user1LoginRequest);
        LoginResponse user1Login3 = authenticationSteps.login(user1LoginRequest);

        StandardResponse<LoginResponse> user1Refresh = refreshTokenSteps.refreshTokensSuccessfully(user1Login1.refreshToken());
        StandardResponse<LoginResponse> user1RefreshChain = refreshTokenSteps.refreshTokensSuccessfully(user1Refresh.data().refreshToken());


        LoginResponse user2Login = authenticationSteps.login(user2LoginRequest);

        registrationSteps.registerSuccessfully();
        LoginResponse loginResponse1 = authenticationSteps.loginAsFixtureUser();
        LoginResponse loginResponse2 = authenticationSteps.loginAsFixtureUser();
        LoginResponse loginResponse3 = authenticationSteps.loginAsFixtureUser();

        StandardResponse<LoginResponse> refreshLoginSuccess = refreshTokenSteps.refreshTokensSuccessfully(loginResponse1.refreshToken()); // успех
        refreshTokenSteps.expectTokenCompromised(loginResponse1.refreshToken()); // компроментация всех токенов пользователя: loginResponse1, loginResponse2, loginResponse3, refreshLoginSuccess
        refreshTokenSteps.expectTokenCompromised(loginResponse2.refreshToken()); // неудача, но по сути ничего не происходит

        LoginResponse loginResponseSuccess = authenticationSteps.loginAsFixtureUser(); // новый вход легитимного пользователя

        refreshTokenSteps.expectTokenCompromised(loginResponse1.refreshToken()); // злоумышленник пытается сбросить сессию

        StandardResponse<LoginResponse> refreshLoginSuccessAfterCompromising = refreshTokenSteps.refreshTokensSuccessfully(loginResponseSuccess.refreshToken()); // успех - компроментированный токен ни на что не влияет
        // попытки использовать другие токены. везде неудача
        refreshTokenSteps.expectTokenCompromised(loginResponse3.refreshToken());
        refreshTokenSteps.expectTokenCompromised(refreshLoginSuccess.data().refreshToken());
        // пользователь может продолжать цепочку обновлений
        refreshTokenSteps.refreshTokensSuccessfully(refreshLoginSuccessAfterCompromising.data().refreshToken());

        // другие пользователи могут продолжать цепочку обновлений
        refreshTokenSteps.refreshTokensSuccessfully(user2Login.refreshToken());
        refreshTokenSteps.refreshTokensSuccessfully(user1Login2.refreshToken());
        refreshTokenSteps.refreshTokensSuccessfully(user1Login3.refreshToken());
        refreshTokenSteps.refreshTokensSuccessfully(user1RefreshChain.data().refreshToken());
    }


}