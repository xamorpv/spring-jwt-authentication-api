package ru.ls.pjwt.tests.refresh.integration;

import org.junit.jupiter.api.Test;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;

public class RefreshTokenRotationIntegrationTest extends WebSecurityTest {
    @Test
    void test() throws Exception {
        refreshTokenHelper.assertSuccessRegistration();
        LoginResponse loginResponse = refreshTokenHelper.login();
        refreshTokenHelper.assertSuccessRefresh(loginResponse);
        refreshTokenHelper.assertFailureRefresh(loginResponse); // повторное использование токена невозможно - он уже использован
    }
}
