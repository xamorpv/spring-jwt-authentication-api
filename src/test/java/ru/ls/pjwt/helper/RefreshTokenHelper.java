package ru.ls.pjwt.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RefreshTokenRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Component
public class RefreshTokenHelper {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthoritiesProperties authoritiesProperties;

    public LoginResponse login() throws Exception {
        return login(StandardUser.loginRequest());
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

    public StandardResponse<LoginResponse> assertSuccessRefresh(LoginResponse loginResponse) throws Exception {
        StandardResponse<LoginResponse> loginResponseStandardResponse = objectMapper.readValue(
                refreshStringBody(loginResponse), new TypeReference<>() {});
        assertTrue(loginResponseStandardResponse.success());
        return loginResponseStandardResponse;
    }

    public void assertFailureRefresh(LoginResponse loginResponse) throws Exception {
        assertRefreshFailure(refreshStringBody(loginResponse));
    }

    private void assertRefreshFailure(String response) {
        StandardResponse<ErrorResponse> errorResponse;
        try {
            errorResponse = objectMapper.readValue(response, new TypeReference<>() {});
        } catch (JacksonException e) {
            fail("response not error, refresh not failed: "+response);
            return;
        }
        assertTrue(errorResponse.message().contains("compromise"));
        assertFalse(errorResponse.success());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.data().statusCode());
    }

    public String refreshStringBody(LoginResponse loginResponse) throws Exception {
        MvcResult refreshTokenRequest = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RefreshTokenRequest(loginResponse.refreshToken()))))
                .andReturn();

        return refreshTokenRequest.getResponse().getContentAsString();
    }

    public void assertSuccessRegistration() throws Exception {
        assertSuccessRegistration(StandardUser.registerRequest());
    }

    public void assertSuccessRegistration(RegisterRequest registerRequest) throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        ).andReturn();
        StandardResponse<RegisterResponse> registerResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertAll(
                ()->assertEquals(registerRequest.username(), registerResponse.data().username()),
                ()->assertEquals(registerRequest.email(), registerResponse.data().email()),
                ()->{
                    // зависимые утверждения
                    assertEquals(1, registerResponse.data().authorities().size());
                    assertEquals(authoritiesProperties.user(), registerResponse.data().authorities().stream().findFirst().orElseThrow());
                }
        );
    }

    public void invalidateRefreshToken(LoginResponse loginResponse) throws Exception {
        mockMvc.perform(post("/api/v1/auth/invalidate-refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(loginResponse.refreshToken()))))
                .andExpect(status().isCreated());
    }
}
