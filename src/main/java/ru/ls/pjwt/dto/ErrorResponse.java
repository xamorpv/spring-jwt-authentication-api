package ru.ls.pjwt.dto;

import java.util.List;

public record ErrorResponse(int statusCode, String timestamp, List<FieldErrorDto> errors) {

}
