package ru.ls.pjwt.common.web.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(T data, String message, boolean success) {

}
