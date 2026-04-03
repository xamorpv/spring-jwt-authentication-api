package ru.ls.pjwt.domain.auth.dto.response;

public record LoginResponse (String refreshToken, String accessToken) {
}
