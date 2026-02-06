package ru.ls.pjwt.dto;

public record ErrorResponse (String message, int statusCode, String errorMessage, String timestamp) {

}
