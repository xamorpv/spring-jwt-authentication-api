package ru.ls.pjwt.dto.auth.request;

import ru.ls.pjwt.validation.ValidBlank;

public record LoginRequest(
        @ValidBlank String username, @ValidBlank String password
) {
}
