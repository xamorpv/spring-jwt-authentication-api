package ru.ls.pjwt.domain.token.service.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.RefreshTokenRequest;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;
import ru.ls.pjwt.steps.user.AuthenticationSteps;

@Import(AuthenticationSteps.class)
public class JwtTokenConfusionIntegrationTest extends WebIntegrationTest {
  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private MockMvcClient mockMvcClient;

  @Test
  void shouldReturn401WhenUsingRefreshTokenInsteadOfAccessToken() throws Exception {
    mockMvcClient.getProtectedDataExpecting401(
        "Bearer " + authenticationSteps.loginAsDevUser().refreshToken());
  }

  @Test
  void shouldReturn401WhenUsingAccessTokenInsteadOfRefreshToken() throws Exception {
    StandardResponse<ErrorResponse> error =
        mockMvcClient.postExpectingError(
            RefreshTokenSteps.ENDPOINT,
            new RefreshTokenRequest(authenticationSteps.loginAsDevUser().accessToken()),
            "Refresh");

    assertEquals(HttpStatus.UNAUTHORIZED.value(), error.data().statusCode());
  }
}
