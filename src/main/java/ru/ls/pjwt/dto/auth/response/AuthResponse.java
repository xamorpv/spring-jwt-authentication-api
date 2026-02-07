package ru.ls.pjwt.dto.auth;

public record AuthResponse(String refreshToken, String accessToken) {
}
