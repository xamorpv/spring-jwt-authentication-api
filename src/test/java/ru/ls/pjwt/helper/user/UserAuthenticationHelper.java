package ru.ls.pjwt.helper.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.property.DevPasswordsProperties;
import ru.ls.pjwt.domain.auth.property.DevUsernamesProperties;
import ru.ls.pjwt.helper.MockMvcHelper;
import ru.ls.pjwt.helper.ObjectMapperHelper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@TestComponent
public class UserAuthenticationHelper {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectMapperHelper objectMapperHelper;

    @Autowired
    private MockMvcHelper mockMvcHelper;

    @Autowired
    private DevPasswordsProperties devPasswordsProperties;

    @Autowired
    private DevUsernamesProperties devUsernamesProperties;

    public LoginResponse login() throws Exception {
        return login(StandardUser.loginRequest());
    }

    public LoginResponse loginAsExistingUser() throws Exception {
        return login(new LoginRequest(devUsernamesProperties.user(), devPasswordsProperties.standard()));
    }

    public LoginResponse loginAsExistingModer() throws Exception {
        return login(new LoginRequest(devUsernamesProperties.moderator(), devPasswordsProperties.standard()));
    }

    public LoginResponse loginAsExistingAdmin() throws Exception {
        return login(new LoginRequest(devUsernamesProperties.admin(), devPasswordsProperties.standard()));
    }

    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        StandardResponse<LoginResponse> loginResponse = objectMapper.readValue(
                loginStringBody(loginRequest), new TypeReference<>() {});

        return loginResponse.data();
    }

    public void assertFailureLogin(LoginRequest loginRequest, int expectedStatusCode) throws Exception {
        assertLoginFailure(loginStringBody(loginRequest), expectedStatusCode);
    }

    public void assertLoginFailure(String response, int failStatusCode) {
        StandardResponse<ErrorResponse> errorResponse = objectMapperHelper.readErrorResponse(response, "Authentication");

        assertAll("Authentication should fail",
                ()->assertFalse(errorResponse.success(), "Success flag must be false"),
                ()->assertEquals(failStatusCode, errorResponse.data().statusCode())
        );
    }

    public String loginStringBody(LoginRequest loginRequest) throws Exception {
        return mockMvcHelper.postForContent("/api/v1/auth/login", loginRequest);
    }
}
