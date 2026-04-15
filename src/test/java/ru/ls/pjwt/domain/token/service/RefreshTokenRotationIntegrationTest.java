package ru.ls.pjwt.domain.token.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Import({RegistrationSteps.class, AuthenticationSteps.class, RefreshTokenSteps.class})
public class RefreshTokenRotationIntegrationTest extends WebIntegrationTest {
    @Autowired
    private RegistrationSteps registrationSteps;

    @Autowired
    private AuthenticationSteps authenticationSteps;

    @Autowired
    private RefreshTokenSteps refreshTokenSteps;

    @DisplayName("Refresh token rotation test")
    @Test
    void testSuccessfulTokenRotationFlow() throws Exception {
        registrationSteps.registerSuccessfully();
        LoginResponse loginResponse = authenticationSteps.loginAsFixtureUser();
        refreshTokenSteps.refreshTokensSuccessfully(loginResponse.refreshToken());
        refreshTokenSteps.expectTokenCompromised(loginResponse.refreshToken()); // повторное использование токена невозможно - он уже использован
    }
}
