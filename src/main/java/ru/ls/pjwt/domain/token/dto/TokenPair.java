package ru.ls.pjwt.domain.token.dto;

public record TokenPair(String refreshToken, String accessToken) {}
