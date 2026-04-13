package ru.ls.pjwt.helper.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.property.DevPasswordsProperties;
import ru.ls.pjwt.domain.auth.property.DevUsernamesProperties;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestComponent
public class UserAuthenticationHelper {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andReturn();
        StandardResponse<LoginResponse> loginResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), new TypeReference<>() {});

        return loginResponse.data();
    }
}
