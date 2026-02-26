package ru.ls.pjwt.dto.auth.request;

import ru.ls.pjwt.validation.ValidBlank;

public record RefreshTokenRequest(
        @ValidBlank String refreshToken) {
}
