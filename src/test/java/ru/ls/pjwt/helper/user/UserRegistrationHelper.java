package ru.ls.pjwt.helper.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.helper.MockMvcHelper;
import ru.ls.pjwt.helper.ObjectMapperHelper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@TestComponent
public class UserRegistrationHelper {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectMapperHelper objectMapperHelper;

    @Autowired
    private MockMvcHelper mockMvcHelper;

    @Autowired
    private AuthoritiesProperties authoritiesProperties;

    public void assertSuccessRegistration() throws Exception {
        assertSuccessRegistration(StandardUser.registerRequest());
    }

    public void assertSuccessRegistration(RegisterRequest registerRequest) throws Exception {
        StandardResponse<RegisterResponse> registerResponse = objectMapper.readValue(
                registerStringBody(registerRequest), new TypeReference<>() {});
        assertAll("Registration properties should be correct",
                ()->assertEquals(registerRequest.username(), registerResponse.data().username()),
                ()->assertEquals(registerRequest.email(), registerResponse.data().email()),
                ()->assertAll("New user should have only USER authority",
                        ()->assertEquals(1, registerResponse.data().authorities().size()),
                        ()->assertEquals(authoritiesProperties.user(), registerResponse.data().authorities().stream().findFirst().orElseThrow())
                )
        );
    }

    public void assertFailureRegistration(RegisterRequest registerRequest, int expectedStatusCode) throws Exception {
        assertRegistrationFailure(registerStringBody(registerRequest), expectedStatusCode);
    }

    private void assertRegistrationFailure(String response, int failStatusCode) {
        StandardResponse<ErrorResponse> errorResponse = objectMapperHelper.readErrorResponse(response, "Registration");

        assertAll("Registration should fail",
                ()->assertFalse(errorResponse.success(), "Success flag must be false"),
                ()->assertEquals(failStatusCode, errorResponse.data().statusCode())
        );
    }

    public String registerStringBody(RegisterRequest registerRequest) throws Exception {
        return mockMvcHelper.postForContent("/api/v1/auth/register", registerRequest);
    }
}
