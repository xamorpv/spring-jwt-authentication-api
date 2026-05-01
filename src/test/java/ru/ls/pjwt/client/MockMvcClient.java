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

/**
 * Utility component for sending HTTP requests through {@link MockMvc} and extracting responses,
 * designed for integration tests.
 *
 * <p>All methods throw {@code Exception} if the underlying MockMvc request fails; such exceptions
 * are treated as test failures and should not be caught.
 */
@TestComponent
public class MockMvcClient {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  /**
   * Performs a POST request and expects a failed {@link StandardResponse} with an {@link
   * ErrorResponse} body.
   *
   * @param endpoint the URL to send the POST request to
   * @param body the request body object (will be serialized to JSON)
   * @param operation a human‑readable name for the operation (used in the assertion error message
   *     if the response cannot be parsed)
   * @return the parsed {@code StandardResponse<ErrorResponse>}
   * @throws Exception if the request fails or the response is unparseable
   */
  public StandardResponse<ErrorResponse> postExpectingError(
      final String endpoint, final Object body, final String operation) throws Exception {
    final String content = post(endpoint, body);

    try {
      return objectMapper.readValue(content, new TypeReference<>() {});
    } catch (JacksonException e) {
      return fail(operation + " should be failed. given response: " + content);
    }
  }

  /**
   * Performs a POST request and returns the raw {@link MockHttpServletResponse}.
   *
   * @param endpoint the URL to send the POST request to
   * @param body the request body object (will be serialized to JSON)
   * @return the raw servlet response
   * @throws Exception if the request fails
   */
  public MockHttpServletResponse postReturningStatus(final String endpoint, final Object body)
      throws Exception {
    return mockMvc
        .perform(
            MockMvcRequestBuilders.post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andReturn()
        .getResponse();
  }

  /**
   * Performs a POST request and returns the response content as a JSON string.
   *
   * @param endpoint the URL to send the POST request to
   * @param body the request body object (will be serialized to JSON)
   * @return the JSON response body as a String
   * @throws Exception if the request fails
   */
  public String post(final String endpoint, final Object body) throws Exception {
    final MvcResult registerResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
            .andReturn();

    return registerResult.getResponse().getContentAsString();
  }

  /**
   * Sends a GET request to the protected test endpoint and asserts that the response status is 401
   * (Unauthorized) with the expected error structure.
   *
   * @param headerValue the value of the {@code Authorization} header (e.g. {@code "Bearer token"})
   * @throws Exception if the request or assertions fail
   */
  public void assertProtectedEndpointReturns401(final String headerValue) throws Exception {
    getProtectedData(headerValue)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.statusCode").value(HttpStatus.UNAUTHORIZED.value()));
  }

  /**
   * Sends a GET request to the protected test endpoint and asserts that the response status is 500
   * (Internal Server Error) with the expected error structure.
   *
   * @param headerValue the value of the {@code Authorization} header
   * @throws Exception if the request or assertions fail
   */
  public void assertProtectedEndpointReturns500(final String headerValue) throws Exception {
    getProtectedData(headerValue)
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.data.statusCode").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

  private ResultActions getProtectedData(final String headerValue) throws Exception {
    return mockMvc.perform(get("/api/v1/test/protected").header("Authorization", headerValue));
  }
}
