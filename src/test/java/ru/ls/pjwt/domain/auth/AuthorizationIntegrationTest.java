package ru.ls.pjwt.domain.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.property.ControllerDevProperties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorizationIntegrationTest extends WebSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ControllerDevProperties controllerDevProperties;

    @Test
    void shouldReturn200WhenRequestingPublicEndpoint() throws Exception {
        performOk("/api/v1/test/public", controllerDevProperties.publicResponse());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/test/protected", "/api/v1/test/user-only",
            "/api/v1/test/moder-only", "/api/v1/test/admin-only"
    })
    void shouldReturn401WhenRequestingProtectedEndpointWithoutToken(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user123")
    void shouldAllowAccessWhenRequestingProtectedEndpointWithUserToken() throws Exception {
       performOk("/api/v1/test/protected", controllerDevProperties.protectedResponse());
    }

    @Test
    void shouldAllowAccessWhenRequestingModerEndpointWithModerToken() throws Exception {
        performOk("/api/v1/test/moder-only",
                controllerDevProperties.moderOnlyResponse(),
                userAuthenticationHelper.loginAsExistingModer().accessToken());
    }

    @Test
    void shouldAllowAccessWhenRequestingAdminEndpointWithAdminToken() throws Exception {
        performOk("/api/v1/test/admin-only",
                controllerDevProperties.adminOnlyResponse(),
                userAuthenticationHelper.loginAsExistingAdmin().accessToken());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/test/user-only", "/api/v1/test/admin-only"
    })
    void shouldReturn403WhenModeratorRequestsOtherAuthorityEndpoints(String endpoint) throws Exception {
        perform403(endpoint, userAuthenticationHelper.loginAsExistingModer().accessToken());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/test/user-only", "/api/v1/test/moder-only"
    })
    void shouldReturn403WhenAdminRequestsOtherAuthorityEndpoints(String endpoint) throws Exception {
        perform403(endpoint, userAuthenticationHelper.loginAsExistingAdmin().accessToken());
    }


    void performOk(String endpoint, String message) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(message));
    }

    void performOk(String endpoint, String message, String token) throws Exception {
        mockMvc.perform(get(endpoint)
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(message));
    }

    void perform403(String endpoint, String token) throws Exception {
        mockMvc.perform(get(endpoint)
                        .header("Authorization", "Bearer "+token))
                .andExpect(status().isForbidden());
    }
}
