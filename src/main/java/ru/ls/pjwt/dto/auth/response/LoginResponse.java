package ru.ls.pjwt.dto.auth.response;

public record LoginResponse (String refreshToken, String accessToken) {
}
