package ru.ls.pjwt.dto.api.response;

import ru.ls.pjwt.dto.FieldErrorDto;

import java.util.List;

public record ErrorResponse(int statusCode, String timestamp, List<FieldErrorDto> errors) {

}
