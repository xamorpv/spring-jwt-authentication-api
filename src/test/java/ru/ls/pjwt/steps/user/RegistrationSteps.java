package ru.ls.pjwt.steps.user;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.fixture.StandardUserFixture;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@TestComponent
public class RegistrationSteps {
  private final String ENDPOINT = "/api/v1/auth/register";

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvcClient mockMvcClient;

  @Autowired private AuthoritiesProperties authoritiesProperties;

  public void registerSuccessfully() throws Exception {
    registerSuccessfully(StandardUserFixture.getDefaultRegisterRequest());
  }

  public void registerSuccessfully(RegisterRequest registerRequest) throws Exception {
    StandardResponse<RegisterResponse> registerResponse =
        objectMapper.readValue(
            mockMvcClient.post(ENDPOINT, registerRequest), new TypeReference<>() {});
    assertAll(
        "Registration properties should be correct",
        () -> assertEquals(registerRequest.username(), registerResponse.data().username()),
        () -> assertEquals(registerRequest.email(), registerResponse.data().email()),
        () ->
            assertAll(
                "New user should have only USER authority",
                () -> assertEquals(1, registerResponse.data().authorities().size()),
                () ->
                    assertEquals(
                        authoritiesProperties.user(),
                        registerResponse.data().authorities().stream().findFirst().orElseThrow())));
  }

  public StandardResponse<ErrorResponse> expectRegistrationFailure(
      RegisterRequest registerRequest, int expectedStatusCode) throws Exception {
    StandardResponse<ErrorResponse> errorResponse =
        mockMvcClient.postExpectingError(ENDPOINT, registerRequest, "Registration");

    assertAll(
        "Registration should fail",
        () -> assertFalse(errorResponse.success(), "Success flag"),
        () -> assertEquals(expectedStatusCode, errorResponse.data().statusCode()));

    return errorResponse;
  }
}
