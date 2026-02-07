package ru.ls.pjwt.dto.auth.response;

public record AuthResponse(String refreshToken, String accessToken) {
}
