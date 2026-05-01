package ru.ls.pjwt.domain.token.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.ls.pjwt.base.WebIntegrationEnvironment;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.steps.token.AccessTokenSteps;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Import({
  RegistrationSteps.class,
  AuthenticationSteps.class,
  RefreshTokenSteps.class,
  AccessTokenSteps.class
})
class RefreshTokenFlowIntegrationTest extends WebIntegrationEnvironment {
  @Autowired private RegistrationSteps registrationSteps;

  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private RefreshTokenSteps refreshTokenSteps;

  @Autowired private AccessTokenSteps accessTokenSteps;

  @DisplayName("Token flow")
  @Test
  void testSuccessfulTokenFlow() throws Exception {
    registrationSteps.registerSuccessfully();

    final LoginResponse loginResponse = authenticationSteps.loginAsFixtureUser();
    accessTokenSteps.assertUserTokenIsValid(loginResponse.accessToken());

    final LoginResponse refreshResponse =
        refreshTokenSteps.refreshTokensSuccessfully(loginResponse.refreshToken());
    accessTokenSteps.assertUserTokenIsValid(refreshResponse.accessToken());

    final LoginResponse refreshResponse2 =
        refreshTokenSteps.refreshTokensSuccessfully(
            refreshResponse.refreshToken()); // проверяем, что повторный refresh также работает
    accessTokenSteps.assertUserTokenIsValid(refreshResponse2.accessToken());

    refreshTokenSteps.invalidateRefreshToken(refreshResponse2.refreshToken());
    refreshTokenSteps.expectTokenCompromised(refreshResponse2.refreshToken());
    accessTokenSteps.assertUserTokenIsValid(
        refreshResponse2.accessToken()); // черный список access токенов не планируется добавлять
  }
}
