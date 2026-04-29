package ru.ls.pjwt.domain.token.service.jwt;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.steps.user.AuthenticationSteps;

@Import(AuthenticationSteps.class)
public class JwtExpirationIntegrationTest extends WebIntegrationTest {
  @MockitoBean private Clock clock;

  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private JwtProperties jwtProperties;

  @Autowired private MockMvcClient mockMvcClient;

  @Test
  void shouldReturn401WhenGivenExpiredAccessToken() throws Exception {
    final Instant now = Instant.now();
    when(clock.instant()).thenReturn(now);

    final LoginResponse loginResponse = authenticationSteps.loginAsDevUser();

    when(clock.instant())
        .thenReturn(
            now.plus(jwtProperties.getAccessTokenExpirationMinutes() + 1, ChronoUnit.MINUTES));

    mockMvcClient.getProtectedDataExpecting401("Bearer " + loginResponse.accessToken());
  }
}
