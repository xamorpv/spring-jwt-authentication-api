package ru.ls.pjwt.helper.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.domain.auth.dto.request.RefreshTokenRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.helper.MockMvcHelper;
import ru.ls.pjwt.helper.ObjectMapperHelper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestComponent
public class RefreshTokenHelper {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvcHelper mockMvcHelper;

    @Autowired
    private ObjectMapperHelper objectMapperHelper;

    public StandardResponse<LoginResponse> assertSuccessRefresh(LoginResponse loginResponse) throws Exception {
        StandardResponse<LoginResponse> loginResponseStandardResponse = objectMapper.readValue(
                refreshStringBody(loginResponse), new TypeReference<>() {});
        assertTrue(loginResponseStandardResponse.success(), "Token refresh should succeed");
        return loginResponseStandardResponse;
    }

    public void assertFailureRefresh(LoginResponse loginResponse) throws Exception {
        assertRefreshFailure(refreshStringBody(loginResponse));
    }

    private void assertRefreshFailure(String response) {
        StandardResponse<ErrorResponse> errorResponse = objectMapperHelper.readErrorResponse(response, "Refresh");

        assertAll("Token refresh should fail",
                ()->assertTrue(errorResponse.message().contains("compromise"), "Message missing 'compromise' keyword"),
                ()->assertFalse(errorResponse.success(), "Success flag must be false"),
                ()->assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.data().statusCode())
        );
    }

    public String refreshStringBody(LoginResponse loginResponse) throws Exception {
        return mockMvcHelper.postForContent("/api/v1/auth/refresh", loginResponse);
    }

    public void invalidateRefreshToken(LoginResponse loginResponse) throws Exception {
        mockMvc.perform(post("/api/v1/auth/invalidate-refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(loginResponse.refreshToken()))))
                .andExpect(status().isOk());
    }
}
