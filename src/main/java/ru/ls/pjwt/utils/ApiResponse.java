package ru.ls.pjwt.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.ls.pjwt.dto.ErrorResponse;
import ru.ls.pjwt.dto.StandardResponse;

@UtilityClass
public class ApiResponse {
    public ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, status.value(), status.getReasonPhrase(), TimeUtils.timestamp()));
    }

    public <T> ResponseEntity<StandardResponse<T>> failure(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new StandardResponse<>(data, message, false));
    }

    public <T> ResponseEntity<StandardResponse<T>> success(T data, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new StandardResponse<>(data, message, true));
    }

    public ResponseEntity<StandardResponse<Void>> success(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new StandardResponse<>(null, message, false));
    }
}
