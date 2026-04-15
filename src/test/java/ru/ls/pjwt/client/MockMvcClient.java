package ru.ls.pjwt.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.fail;

@TestComponent
public class MockMvcClient {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public StandardResponse<ErrorResponse> postExpectingError(String endpoint, Object body, String operation) throws Exception {
        String content = post(endpoint, body);

        try {
            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (JacksonException e) {
            return fail(operation+" should be failed. given response: "+content);
        }
    }

    public MockHttpServletResponse postReturningStatus(String endpoint, Object body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))).andReturn().getResponse();
    }

    public String post(String endpoint, Object body) throws Exception {
        MvcResult registerResult = mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        ).andReturn();

        return registerResult.getResponse().getContentAsString();
    }
}
