package ru.ls.pjwt.steps.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.property.DevPasswordsProperties;
import ru.ls.pjwt.domain.auth.property.DevUsernamesProperties;
import ru.ls.pjwt.fixture.StandardUserFixture;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@TestComponent
public class AuthenticationSteps {
  private final String ENDPOINT = "/api/v1/auth/login";

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvcClient mockMvcClient;

  @Autowired private DevPasswordsProperties devPasswordsProperties;

  @Autowired private DevUsernamesProperties devUsernamesProperties;

  public LoginResponse loginAsFixtureUser() throws Exception {
    return login(StandardUserFixture.getDefaultLoginRequest());
  }

  public LoginResponse loginAsDevUser() throws Exception {
    return login(
        new LoginRequest(devUsernamesProperties.user(), devPasswordsProperties.standard()));
  }

  public LoginResponse loginAsDevModer() throws Exception {
    return login(
        new LoginRequest(devUsernamesProperties.moderator(), devPasswordsProperties.standard()));
  }

  public LoginResponse loginAsDevAdmin() throws Exception {
    return login(
        new LoginRequest(devUsernamesProperties.admin(), devPasswordsProperties.standard()));
  }

  public LoginResponse login(LoginRequest loginRequest) throws Exception {
    StandardResponse<LoginResponse> loginResponse =
        objectMapper.readValue(
            mockMvcClient.post(ENDPOINT, loginRequest), new TypeReference<>() {});

    return loginResponse.data();
  }

  public void expectLoginFailure(LoginRequest loginRequest, int expectedStatusCode)
      throws Exception {
    StandardResponse<ErrorResponse> errorResponse =
        mockMvcClient.postExpectingError(ENDPOINT, loginRequest, "Authentication");

    assertAll(
        "Authentication should fail",
        () -> assertFalse(errorResponse.success(), "Success flag must be false"),
        () -> assertEquals(expectedStatusCode, errorResponse.data().statusCode()));
  }
}
