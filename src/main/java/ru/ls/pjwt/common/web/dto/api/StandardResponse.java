package ru.ls.pjwt.common.web.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(@Nullable T data, String message, boolean success) {}
