package ru.ls.pjwt.common.web.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(T data, String message, boolean success) {

}
