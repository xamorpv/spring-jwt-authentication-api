package ru.ls.pjwt.steps.token;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.domain.auth.property.ControllerDevProperties;

/**
 * Test utility for asserting access token permissions against test endpoints.
 *
 * <p>Provides reusable steps for verifying that a given token grants authenticated access, allows
 * user-specific access, and denies admin access.
 */
@TestComponent
public class AccessTokenSteps {
  @Autowired private MockMvc mockMvc;

  @Autowired private ControllerDevProperties controllerDevProperties;

  /**
   * Asserts that the given token is valid for a non-admin user: it grants access to both the
   * protected and user-only endpoints, but not to the admin-only endpoint.
   *
   * @param token the access token (without "Bearer " prefix)
   * @throws Exception if any MockMvc assertion fails
   */
  public void assertUserTokenIsValid(String token) throws Exception {
    assertTokenGrantsUserAccess(token);
    assertTokenGrantsAccess(token);
    assertTokenNotGrantsAdminAccess(token);
  }

  private void assertTokenGrantsAccess(String token) throws Exception {
    mockMvc
        .perform(get("/api/v1/test/protected").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(controllerDevProperties.protectedResponse()));
  }

  private void assertTokenGrantsUserAccess(String token) throws Exception {
    mockMvc
        .perform(get("/api/v1/test/user-only").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(controllerDevProperties.userOnlyResponse()));
  }

  private void assertTokenNotGrantsAdminAccess(String token) throws Exception {
    mockMvc
        .perform(get("/api/v1/test/admin-only").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }
}
