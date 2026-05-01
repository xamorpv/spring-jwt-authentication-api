package ru.ls.pjwt.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.ls.pjwt.base.WebIntegrationEnvironment;
import ru.ls.pjwt.steps.user.AuthenticationSteps;

@Import(AuthenticationSteps.class)
class NonExistentIntegrationTest extends WebIntegrationEnvironment {
  @Autowired private MockMvc mockMvc;

  @Autowired private AuthenticationSteps authenticationSteps;

  @Test
  void shouldReturn401WhenRequestingNonExistentEndpointWithoutAccessToken() throws Exception {
    mockMvc
        .perform(get("/nonexistent"))
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturn404WhenRequestingNonExistentEndpointWithAccessToken() throws Exception {
    final String accessToken = authenticationSteps.loginAsDevUser().accessToken();
    mockMvc
        .perform(get("/nonexistent").header("Authorization", "Bearer " + accessToken))
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(status().isNotFound());
  }
}
