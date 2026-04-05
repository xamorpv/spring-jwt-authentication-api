package ru.ls.pjwt.domain.auth.dto.request;

import ru.ls.pjwt.common.web.validation.ValidBlank;

public record RefreshTokenRequest(
        @ValidBlank String refreshToken) {
}
