package ru.ls.pjwt.dto.api.response;

public record LoginResponse (String refreshToken, String accessToken) {
}
