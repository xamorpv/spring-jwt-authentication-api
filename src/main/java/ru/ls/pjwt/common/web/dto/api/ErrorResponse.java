package ru.ls.pjwt.common.web.dto.api;

import java.util.List;
import ru.ls.pjwt.common.web.dto.exception.FieldErrorDto;

public record ErrorResponse(int statusCode, String timestamp, List<FieldErrorDto> errors) {}
