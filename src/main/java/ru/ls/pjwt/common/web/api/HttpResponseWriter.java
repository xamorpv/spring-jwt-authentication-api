package ru.ls.pjwt.common.web.api;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.api.StandardResponse;
import ru.ls.pjwt.common.util.JsonUtils;
import ru.ls.pjwt.common.util.TimeUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class HttpResponseWriter {
    private final TimeUtils timeUtils;
    private final JsonUtils jsonUtils;

    public void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        jsonUtils.writeValue(response,
                new StandardResponse<>(new ErrorResponse(status.value(), timeUtils.timestamp(), null), message, false),
                status.value());
    }
}
