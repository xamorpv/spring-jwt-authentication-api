package ru.ls.pjwt.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.dto.api.response.ErrorResponse;
import ru.ls.pjwt.dto.FieldErrorDto;
import ru.ls.pjwt.dto.api.response.StandardResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiResponse {
    private final TimeUtils timeUtils;

    public ResponseEntity<StandardResponse<ErrorResponse>> error(HttpStatus status, String message) {
        return errorInFields(status, message, null);
    }

    public ResponseEntity<StandardResponse<ErrorResponse>> errorInFields(HttpStatus status, String message, List<FieldErrorDto> errors) {
        return failure(new ErrorResponse(status.value(), timeUtils.timestamp(), errors), message, status);
    }

    public <T> ResponseEntity<StandardResponse<T>> failure(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new StandardResponse<>(data, message, false));
    }

    public <T> ResponseEntity<StandardResponse<T>> success(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new StandardResponse<>(data, message, true));
    }

    public ResponseEntity<StandardResponse<Void>> success(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new StandardResponse<>(null, message, true));
    }
}
