package ru.ls.pjwt.steps.token;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestComponent
public class RefreshTokenSteps {
    public static final String ENDPOINT = "/api/v1/auth/refresh";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvcClient mockMvcClient;

    public StandardResponse<LoginResponse> refreshTokensSuccessfully(String refreshToken) throws Exception {
        StandardResponse<LoginResponse> loginResponseStandardResponse = objectMapper.readValue(
                refreshReturningStringBody(refreshToken), new TypeReference<>() {});
        assertTrue(loginResponseStandardResponse.success(), "Token refresh should succeed");
        return loginResponseStandardResponse;
    }

    public void refreshExpectingError(String refreshToken, HttpStatus expectedStatus) throws Exception {
        StandardResponse<ErrorResponse> errorResponse = mockMvcClient.postExpectingError(
                ENDPOINT, new RefreshTokenRequest(refreshToken), "Refresh");

        assertEquals(expectedStatus.value(), errorResponse.data().statusCode(), "status code");
    }

    public void expectTokenCompromised(String refreshToken) throws Exception {
        StandardResponse<ErrorResponse> errorResponse = mockMvcClient.postExpectingError(
                ENDPOINT, new RefreshTokenRequest(refreshToken), "Refresh");

        assertAll("Token refresh should fail",
                ()->assertTrue(errorResponse.message().contains("compromise"), "Message missing 'compromise' keyword"),
                ()->assertFalse(errorResponse.success(), "Success flag must be false"),
                ()->assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.data().statusCode())
        );
    }

    public String refreshReturningStringBody(String refreshToken) throws Exception {
        return mockMvcClient.post(ENDPOINT, new RefreshTokenRequest(refreshToken));
    }

    public void invalidateRefreshToken(String refreshToken) throws Exception {
        mockMvc.perform(post("/api/v1/auth/invalidate-refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isOk());
    }
}
