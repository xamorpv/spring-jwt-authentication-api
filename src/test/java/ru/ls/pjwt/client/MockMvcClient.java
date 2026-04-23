package ru.ls.pjwt.client;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@TestComponent
public class MockMvcClient {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  public StandardResponse<ErrorResponse> postExpectingError(
      String endpoint, Object body, String operation) throws Exception {
    String content = post(endpoint, body);

    try {
      return objectMapper.readValue(content, new TypeReference<>() {});
    } catch (JacksonException e) {
      return fail(operation + " should be failed. given response: " + content);
    }
  }

  public MockHttpServletResponse postReturningStatus(String endpoint, Object body)
      throws Exception {
    return mockMvc
        .perform(
            MockMvcRequestBuilders.post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andReturn()
        .getResponse();
  }

  public String post(String endpoint, Object body) throws Exception {
    MvcResult registerResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
            .andReturn();

    return registerResult.getResponse().getContentAsString();
  }

  public void getProtectedDataExpecting401(String headerValue) throws Exception {
    getProtectedData(headerValue)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.statusCode").value(HttpStatus.UNAUTHORIZED.value()));
  }

  public void getProtectedDataExpecting500(String headerValue) throws Exception {
    getProtectedData(headerValue)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.statusCode").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  private ResultActions getProtectedData(String headerValue) throws Exception {
    return mockMvc.perform(get("/api/v1/test/protected").header("Authorization", headerValue));
  }
}
