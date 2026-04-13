package ru.ls.pjwt.domain.token.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;

public class RefreshTokenFlowIntegrationTest extends WebSecurityTest {
    @DisplayName("Token flow")
    @Test
    void testSuccessfulTokenFlow() throws Exception {
        userRegistrationHelper.assertSuccessRegistration();

        LoginResponse loginResponse = userAuthenticationHelper.login();
        accessTokenHelper.assertUserTokenIsValid(loginResponse.accessToken());

        LoginResponse refreshResponse = refreshTokenHelper.assertSuccessRefresh(loginResponse).data();
        accessTokenHelper.assertUserTokenIsValid(refreshResponse.accessToken());

        LoginResponse refreshResponse2 = refreshTokenHelper.assertSuccessRefresh(refreshResponse).data(); // проверяем, что повторный refresh также работает
        accessTokenHelper.assertUserTokenIsValid(refreshResponse2.accessToken());

        refreshTokenHelper.invalidateRefreshToken(refreshResponse2);
        refreshTokenHelper.assertFailureRefresh(refreshResponse2);
        accessTokenHelper.assertUserTokenIsValid(refreshResponse2.accessToken()); // черный список access токенов не планируется добавлять
    }
}
