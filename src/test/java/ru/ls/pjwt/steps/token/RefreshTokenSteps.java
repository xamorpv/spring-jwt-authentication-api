package ru.ls.pjwt.steps.token;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.RefreshTokenRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Test steps for refresh token operations.
 *
 * <p>Encapsulates common assertions and request building for the {@code /api/v1/auth/refresh} and
 * invalidation endpoints.
 */
@Slf4j
@TestComponent
public class RefreshTokenSteps {
  public static final String ENDPOINT = "/api/v1/auth/refresh";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvcClient mockMvcClient;

  /**
   * Refreshes tokens using the given refresh token and expects a successful response containing a
   * new token pair.
   *
   * @param refreshToken the current refresh token string
   * @return the {@link LoginResponse} with the new access and refresh tokens
   * @throws Exception if the request fails or the response does not indicate success
   */
  public LoginResponse refreshTokensSuccessfully(String refreshToken) throws Exception {
    StandardResponse<LoginResponse> loginResponseStandardResponse =
        objectMapper.readValue(refreshReturningStringBody(refreshToken), new TypeReference<>() {});
    assertTrue(loginResponseStandardResponse.success(), "Token refresh should succeed");
    LoginResponse loginResponse = loginResponseStandardResponse.data();
    assertNotNull(loginResponse, "login response after success refresh");
    return loginResponse;
  }

  /**
   * Attempts to refresh tokens and expects a failed response with a specific HTTP status.
   *
   * @param refreshToken the refresh token string
   * @param expectedStatus the expected {@link HttpStatus} of the response
   * @throws Exception if the request fails or the status code does not match
   */
  public void refreshExpectingError(String refreshToken, HttpStatus expectedStatus)
      throws Exception {
    StandardResponse<ErrorResponse> standardRespose =
        mockMvcClient.postExpectingError(
            ENDPOINT, new RefreshTokenRequest(refreshToken), "Refresh");

    ErrorResponse errorResponse = standardRespose.data();
    assertNotNull(errorResponse, "error response");

    assertEquals(expectedStatus.value(), errorResponse.statusCode(), "status code");
  }

  /**
   * Attempts to refresh tokens and expects that the token has been compromised.
   *
   * <p>The assertion verifies that the response indicates a failure with {@code UNAUTHORIZED}
   * status and that the error message contains the word "compromise".
   *
   * @param refreshToken the refresh token string
   * @throws Exception if the request fails or the response does not signal a compromised token
   */
  public void expectTokenCompromised(String refreshToken) throws Exception {
    StandardResponse<ErrorResponse> standardResponse =
        mockMvcClient.postExpectingError(
            ENDPOINT, new RefreshTokenRequest(refreshToken), "Refresh");

    ErrorResponse errorResponse = standardResponse.data();
    assertNotNull(errorResponse, "error response");

    assertAll(
        "Token refresh should fail",
        () ->
            assertTrue(
                standardResponse.message().contains("compromise"),
                "Message missing 'compromise' keyword"),
        () -> assertFalse(standardResponse.success(), "Success flag must be false"),
        () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.statusCode()));
  }

  /**
   * Calls the refresh endpoint and returns the raw JSON response body.
   *
   * @param refreshToken the refresh token string
   * @return the JSON response body as a {@code String}
   * @throws Exception if the request fails
   */
  public String refreshReturningStringBody(String refreshToken) throws Exception {
    return mockMvcClient.post(ENDPOINT, new RefreshTokenRequest(refreshToken));
  }

  /**
   * Invalidates the given refresh token by marking it as used.
   *
   * @param refreshToken the refresh token string to invalidate
   * @throws Exception if the request fails
   */
  public void invalidateRefreshToken(String refreshToken) throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/invalidate-refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
        .andExpect(status().isOk());
  }
}
