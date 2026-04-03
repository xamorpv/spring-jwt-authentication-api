package ru.ls.pjwt.dto.api.request;

import ru.ls.pjwt.validation.ValidBlank;

public record LoginRequest(
        @ValidBlank String username, @ValidBlank String password
) {
}
