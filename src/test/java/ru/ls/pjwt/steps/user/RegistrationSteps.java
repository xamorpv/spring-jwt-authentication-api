package ru.ls.pjwt.steps.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    RegisterResponse data = registerResponse.data();
    assertNotNull(data, "successful register response data");
    assertAll(
        "Registration properties should be correct",
        () -> assertEquals(registerRequest.username(), data.username()),
        () -> assertEquals(registerRequest.email(), data.email()),
        () ->
            assertAll(
                "New user should have only USER authority",
                () -> assertEquals(1, data.authorities().size()),
                () ->
                    assertEquals(
                        authoritiesProperties.user(),
                        data.authorities().stream().findFirst().orElseThrow())));
  }

  public StandardResponse<ErrorResponse> expectRegistrationFailure(
      RegisterRequest registerRequest, int expectedStatusCode) throws Exception {
    StandardResponse<ErrorResponse> standardResponse =
        mockMvcClient.postExpectingError(ENDPOINT, registerRequest, "Registration");

    ErrorResponse errorResponse = standardResponse.data();
    assertNotNull(errorResponse, "errorResponse data");
    assertAll(
        "Registration should fail",
        () -> assertFalse(standardResponse.success(), "Success flag"),
        () -> assertEquals(expectedStatusCode, errorResponse.statusCode()));

    return standardResponse;
  }
}
