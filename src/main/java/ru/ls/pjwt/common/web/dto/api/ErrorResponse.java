package ru.ls.pjwt.common.web.dto.api;

import ru.ls.pjwt.common.web.dto.exception.FieldErrorDto;

import java.util.List;

public record ErrorResponse(int statusCode, String timestamp, List<FieldErrorDto> errors) {

}
