package ru.ls.pjwt.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.dto.ErrorResponse;
import ru.ls.pjwt.dto.StandardResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JsonApiResponse {
    private final TimeUtils timeUtils;
    private final JsonUtils jsonUtils;

    public void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        jsonUtils.writeValue(response,
                new StandardResponse<>(new ErrorResponse(status.value(), timeUtils.timestamp(), null), message, false),
                status.value());
    }
}
