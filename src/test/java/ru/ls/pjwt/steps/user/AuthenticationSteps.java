package ru.ls.pjwt.steps.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

/**
 * Reusable test steps for user authentication.
 *
 * <p>Provides convenience methods for logging in as various dev users or the standard fixture user,
 * and for asserting login failures.
 */
@TestComponent
public class AuthenticationSteps {
  private final String ENDPOINT = "/api/v1/auth/login";

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvcClient mockMvcClient;

  @Autowired private DevPasswordsProperties devPasswordsProperties;

  @Autowired private DevUsernamesProperties devUsernamesProperties;

  /**
   * Logs in as the standard fixture user and returns the resulting token pair.
   *
   * @return the {@link LoginResponse} with access and refresh tokens
   * @throws Exception if the request fails or the response is not a valid success response
   */
  public LoginResponse loginAsFixtureUser() throws Exception {
    return login(StandardUserFixture.getDefaultLoginRequest());
  }

  /**
   * Logs in as the default dev user and returns the resulting token pair.
   *
   * @return the {@link LoginResponse} with access and refresh tokens
   * @throws Exception if the request fails or the response is not a valid success response
   */
  public LoginResponse loginAsDevUser() throws Exception {
    return login(
        new LoginRequest(devUsernamesProperties.user(), devPasswordsProperties.standard()));
  }

  /**
   * Logs in as the dev moderator and returns the resulting token pair.
   *
   * @return the {@link LoginResponse} with access and refresh tokens
   * @throws Exception if the request fails or the response is not a valid success response
   */
  public LoginResponse loginAsDevModer() throws Exception {
    return login(
        new LoginRequest(devUsernamesProperties.moderator(), devPasswordsProperties.standard()));
  }

  /**
   * Logs in as the dev admin and returns the resulting token pair.
   *
   * @return the {@link LoginResponse} with access and refresh tokens
   * @throws Exception if the request fails or the response is not a valid success response
   */
  public LoginResponse loginAsDevAdmin() throws Exception {
    return login(
        new LoginRequest(devUsernamesProperties.admin(), devPasswordsProperties.standard()));
  }

  /**
   * Performs a login request and returns the parsed {@link LoginResponse}.
   *
   * @param loginRequest the login credentials
   * @return the {@code LoginResponse} with access and refresh tokens
   * @throws Exception if the request fails or the response is not a valid success response
   */
  public LoginResponse login(LoginRequest loginRequest) throws Exception {
    StandardResponse<LoginResponse> standardResponse =
        objectMapper.readValue(
            mockMvcClient.post(ENDPOINT, loginRequest), new TypeReference<>() {});

    LoginResponse loginResponse = standardResponse.data();
    assertNotNull(loginResponse, "loginResponse data");
    return loginResponse;
  }

  /**
   * Attempts a login and expects a failure response with the given HTTP status code.
   *
   * @param loginRequest the login credentials
   * @param expectedStatusCode the expected HTTP status code (e.g. 401)
   * @throws Exception if the request fails or the response does not match the expected failure
   *     structure
   */
  public void expectLoginFailure(LoginRequest loginRequest, int expectedStatusCode)
      throws Exception {
    StandardResponse<ErrorResponse> standardResponse =
        mockMvcClient.postExpectingError(ENDPOINT, loginRequest, "Authentication");

    ErrorResponse errorResponse = standardResponse.data();
    assertNotNull(errorResponse, "error response");

    assertAll(
        "Authentication should fail",
        () -> assertFalse(standardResponse.success(), "Success flag must be false"),
        () -> assertEquals(expectedStatusCode, errorResponse.statusCode()));
  }
}
