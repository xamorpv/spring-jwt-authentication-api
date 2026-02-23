package ru.ls.pjwt.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import ru.ls.pjwt.utils.constants.Validations;

public record RefreshTokenRequest(
        @NotBlank(message = Validations.blank) String refreshToken) {
}
