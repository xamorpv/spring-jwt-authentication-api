package ru.ls.pjwt.domain.token.service.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Slf4j
@Import({AuthenticationSteps.class, RegistrationSteps.class})
public class JwtTokenMalformedIntegrationTest extends WebIntegrationTest {
  @Autowired private MockMvcClient mockMvcClient;

  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private RegistrationSteps registrationSteps;

  @Test
  void shouldReturn401WhenGivenTokenWithWrongSignature() throws Exception {
    final String accessToken = authenticationSteps.loginAsDevUser().accessToken();
    final String invalidToken = accessToken.substring(0, accessToken.lastIndexOf('.'));

    registrationSteps.registerSuccessfully();
    final String otherAccessToken = authenticationSteps.loginAsFixtureUser().accessToken();
    final String invalidSignature = otherAccessToken.substring(accessToken.lastIndexOf('.'));

    final String malformedToken =
        invalidToken
            + invalidSignature; // получился токен с payload одного пользователя и signature другого
    // пользователя

    mockMvcClient.getProtectedDataExpecting401("Bearer " + malformedToken);
  }

  @Test
  void shouldReturn401WhenNoneAttack() throws Exception {
    final String adminAccessToken = authenticationSteps.loginAsDevAdmin().accessToken();
    final String adminPayload =
        adminAccessToken.substring(
            adminAccessToken.indexOf(".") + 1, adminAccessToken.lastIndexOf("."));
    final String headerWithNoneAlgorithm = "eyJhbGciOiAibm9uZSIsICJ0eXAiOiAiSldUIn0";

    final String fakeAccessToken = headerWithNoneAlgorithm + "." + adminPayload + ".";

    mockMvcClient.getProtectedDataExpecting401("Bearer " + fakeAccessToken);
  }
}
