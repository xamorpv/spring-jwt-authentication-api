package ru.ls.pjwt.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.fail;

@Component
public class ObjectMapperHelper {
    @Autowired
    private ObjectMapper objectMapper;

    public StandardResponse<ErrorResponse> readErrorResponse(String body, String operation) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (JacksonException e) {
            return fail(operation+" should be failed. given response: "+body);
        }
    }
}
