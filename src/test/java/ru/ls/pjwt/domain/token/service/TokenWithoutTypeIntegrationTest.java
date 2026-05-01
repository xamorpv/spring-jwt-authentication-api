package ru.ls.pjwt.domain.token.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationEnvironment;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import ru.ls.pjwt.fixture.JwtTokenFixture;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;

@Import(RefreshTokenSteps.class)
class TokenWithoutTypeIntegrationTest extends WebIntegrationEnvironment {
  @Autowired private MockMvcClient mockMvcClient;

  @Autowired private AuthoritiesProperties authoritiesProperties;

  @Autowired private JwtTokenFixture jwtTokenFixture;

  @Autowired private RefreshTokenSteps refreshTokenSteps;

  @Test
  void shouldReturn500WhenGivenAccessTokenWithoutType() throws Exception {
    final String jwtWithoutType =
        jwtTokenFixture
            .getBaseJwtBuilder()
            .subject("username")
            .claim("authorities", List.of(authoritiesProperties.user()))
            .compact();

    mockMvcClient.assertProtectedEndpointReturns500("Bearer " + jwtWithoutType);
  }

  @Test
  void shouldReturn500WhenGivenRefreshTokenWithoutType() throws Exception {
    final String jwtWithoutType = jwtTokenFixture.getBaseJwtBuilder().subject("username").compact();
    refreshTokenSteps.refreshExpectingError(jwtWithoutType, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
