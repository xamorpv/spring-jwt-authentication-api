package ru.ls.pjwt.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;


@UtilityClass
public class JsonUtils {
    public final ObjectMapper objectMapper = new ObjectMapper();

    public void writeValue(HttpServletResponse response, Object body, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
