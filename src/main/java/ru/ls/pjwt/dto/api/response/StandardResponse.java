package ru.ls.pjwt.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(T data, String message, boolean success) {

}
