package ru.ls.pjwt.domain.token.service;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.domain.auth.property.DevUsernamesProperties;
import ru.ls.pjwt.fixture.JwtTokenFixture;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;

@Import({RefreshTokenSteps.class})
public class TokenWithIncorrectClaimsIntegrationTest extends WebIntegrationTest {
  @Autowired private JwtTokenFixture jwtTokenFixture;
  @Autowired private JwtProperties jwtProperties;
  @Autowired private RefreshTokenSteps refreshTokenSteps;
  @Autowired private MockMvcClient mockMvcClient;
  @Autowired private DevUsernamesProperties devUsernamesProperties;

  @Test
  void shouldReturn401WhenGivenRefreshTokenWithoutUUID() throws Exception {
    String refreshTokenWithoutUUID =
        jwtTokenFixture
            .getBaseJwtBuilder()
            .subject(devUsernamesProperties.user())
            .claim("type", jwtProperties.getRefreshToken())
            .compact();
    refreshTokenSteps.refreshExpectingError(refreshTokenWithoutUUID, HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenGivenAccessTokenWithoutSubject() throws Exception {
    String accessTokenWithoutSubject =
        jwtTokenFixture.getBaseJwtBuilder().claim("type", jwtProperties.getAccessToken()).compact();

    mockMvcClient.getProtectedDataExpecting401("Bearer " + accessTokenWithoutSubject);
  }

  @Test
  void shouldReturn401WhenGivenRefreshTokenWithoutSubject() throws Exception {
    String accessTokenWithoutSubject =
        jwtTokenFixture
            .getBaseJwtBuilder()
            .claim("type", jwtProperties.getRefreshToken())
            .claim("uuid", UUID.randomUUID().toString())
            .compact();

    mockMvcClient.getProtectedDataExpecting401("Bearer " + accessTokenWithoutSubject);
  }
}
