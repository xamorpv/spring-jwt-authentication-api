package ru.ls.pjwt.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.dto.ErrorResponse;
import ru.ls.pjwt.dto.StandardResponse;

import java.io.IOException;

@UtilityClass
public class JsonApiResponse {
    public void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        JsonUtils.writeValue(response,
                new StandardResponse<>(new ErrorResponse(status.value(), status.getReasonPhrase(), TimeUtils.timestamp()), message, false),
                status.value());
    }
}
