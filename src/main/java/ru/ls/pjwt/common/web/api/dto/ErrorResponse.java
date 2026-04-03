package ru.ls.pjwt.common.web.api.dto;

import ru.ls.pjwt.common.web.exception.dto.FieldErrorDto;

import java.util.List;

public record ErrorResponse(int statusCode, String timestamp, List<FieldErrorDto> errors) {

}
