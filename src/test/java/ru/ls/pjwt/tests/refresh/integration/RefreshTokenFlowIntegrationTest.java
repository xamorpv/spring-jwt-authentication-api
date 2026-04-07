package ru.ls.pjwt.tests.refresh.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.helper.AccessTokenHelper;

public class RefreshTokenFlowIntegrationTest extends WebSecurityTest {
    @Autowired
    private AccessTokenHelper accessTokenHelper;
    @Test
    void test() throws Exception {
        refreshTokenHelper.assertSuccessRegistration();

        LoginResponse loginResponse = refreshTokenHelper.login();
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
