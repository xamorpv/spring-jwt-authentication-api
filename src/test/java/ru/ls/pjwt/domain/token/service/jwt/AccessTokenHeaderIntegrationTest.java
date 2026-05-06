package ru.ls.pjwt.domain.token.service.jwt;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ls.pjwt.base.WebIntegrationEnvironment;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.fixture.StandardUserFixture;

class AccessTokenHeaderIntegrationTest extends WebIntegrationEnvironment {
  @Autowired private MockMvcClient mockMvcClient;

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Bearer ",
        "Basic " + StandardUserFixture.DEFAULT_USERNAME + ":" + StandardUserFixture.DEFAULT_PASSWORD
      })
  void shouldReturn401WhenGivenWrongHeader(final String headerValue) throws Exception {
    mockMvcClient.assertProtectedEndpointReturns401(headerValue);
  }
}
