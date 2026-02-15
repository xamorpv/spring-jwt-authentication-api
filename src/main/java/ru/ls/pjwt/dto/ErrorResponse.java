package ru.ls.pjwt.dto;

public record ErrorResponse (int statusCode, String errorMessage, String timestamp) {

}
