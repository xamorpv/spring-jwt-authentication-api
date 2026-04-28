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

@Slf4j
@TestComponent
public class RefreshTokenSteps {
  public static final String ENDPOINT = "/api/v1/auth/refresh";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvcClient mockMvcClient;

  public LoginResponse refreshTokensSuccessfully(String refreshToken) throws Exception {
    StandardResponse<LoginResponse> loginResponseStandardResponse =
        objectMapper.readValue(refreshReturningStringBody(refreshToken), new TypeReference<>() {});
    assertTrue(loginResponseStandardResponse.success(), "Token refresh should succeed");
    LoginResponse loginResponse = loginResponseStandardResponse.data();
    assertNotNull(loginResponse, "login response after success refresh");
    return loginResponse;
  }

  public void refreshExpectingError(String refreshToken, HttpStatus expectedStatus)
      throws Exception {
    StandardResponse<ErrorResponse> standardRespose =
        mockMvcClient.postExpectingError(
            ENDPOINT, new RefreshTokenRequest(refreshToken), "Refresh");

    ErrorResponse errorResponse = standardRespose.data();
    assertNotNull(errorResponse, "error response");

    assertEquals(expectedStatus.value(), errorResponse.statusCode(), "status code");
  }

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

  public String refreshReturningStringBody(String refreshToken) throws Exception {
    return mockMvcClient.post(ENDPOINT, new RefreshTokenRequest(refreshToken));
  }

  public void invalidateRefreshToken(String refreshToken) throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/invalidate-refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
        .andExpect(status().isOk());
  }
}
