package ru.ls.pjwt.dto.api.request;

import ru.ls.pjwt.validation.ValidBlank;

public record RefreshTokenRequest(
        @ValidBlank String refreshToken) {
}
