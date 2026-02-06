package ru.ls.pjwt.dto;

public record FieldErrorDto(String field, Object value, String message) {
}
