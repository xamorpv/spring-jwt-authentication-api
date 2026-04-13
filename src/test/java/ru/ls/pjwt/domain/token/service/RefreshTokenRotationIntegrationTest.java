package ru.ls.pjwt.domain.token.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;

public class RefreshTokenRotationIntegrationTest extends WebSecurityTest {
    @DisplayName("Refresh token rotation test")
    @Test
    void testSuccessfulTokenRotationFlow() throws Exception {
        userRegistrationHelper.assertSuccessRegistration();
        LoginResponse loginResponse = userAuthenticationHelper.login();
        refreshTokenHelper.assertSuccessRefresh(loginResponse);
        refreshTokenHelper.assertFailureRefresh(loginResponse); // повторное использование токена невозможно - он уже использован
    }
}
