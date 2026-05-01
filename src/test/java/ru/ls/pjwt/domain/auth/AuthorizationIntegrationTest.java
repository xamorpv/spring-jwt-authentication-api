package ru.ls.pjwt.domain.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.domain.auth.property.ControllerDevProperties;
import ru.ls.pjwt.steps.user.AuthenticationSteps;

@Import(AuthenticationSteps.class)
class AuthorizationIntegrationTest extends WebIntegrationTest {
  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private MockMvc mockMvc;

  @Autowired private ControllerDevProperties controllerDevProperties;

  @Test
  void shouldReturn200WhenRequestingPublicEndpoint() throws Exception {
    performOk("/api/v1/test/public", controllerDevProperties.publicResponse());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/api/v1/test/protected", "/api/v1/test/user-only",
        "/api/v1/test/moder-only", "/api/v1/test/admin-only"
      })
  void shouldReturn401WhenRequestingProtectedEndpointWithoutToken(final String endpoint)
      throws Exception {
    mockMvc.perform(get(endpoint)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "user123")
  void shouldAllowAccessWhenRequestingProtectedEndpointWithUserToken() throws Exception {
    performOk("/api/v1/test/protected", controllerDevProperties.protectedResponse());
  }

  @Test
  void shouldAllowAccessWhenRequestingModerEndpointWithModerToken() throws Exception {
    performOk(
        "/api/v1/test/moder-only",
        controllerDevProperties.moderOnlyResponse(),
        authenticationSteps.loginAsDevModer().accessToken());
  }

  @Test
  void shouldAllowAccessWhenRequestingAdminEndpointWithAdminToken() throws Exception {
    performOk(
        "/api/v1/test/admin-only",
        controllerDevProperties.adminOnlyResponse(),
        authenticationSteps.loginAsDevAdmin().accessToken());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/v1/test/user-only", "/api/v1/test/admin-only"})
  void shouldReturn403WhenModeratorRequestsOtherAuthorityEndpoints(final String endpoint)
      throws Exception {
    perform403(endpoint, authenticationSteps.loginAsDevModer().accessToken());
  }

  @ParameterizedTest
  @ValueSource(strings = {"/api/v1/test/user-only", "/api/v1/test/moder-only"})
  void shouldReturn403WhenAdminRequestsOtherAuthorityEndpoints(final String endpoint)
      throws Exception {
    perform403(endpoint, authenticationSteps.loginAsDevAdmin().accessToken());
  }

  private void performOk(final String endpoint, final String message) throws Exception {
    mockMvc
        .perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(message));
  }

  private void performOk(final String endpoint, final String message, final String token)
      throws Exception {
    mockMvc
        .perform(get(endpoint).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(message));
  }

  private void perform403(final String endpoint, final String token) throws Exception {
    mockMvc
        .perform(get(endpoint).header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }
}
