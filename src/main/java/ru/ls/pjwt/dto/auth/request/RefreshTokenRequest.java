package ru.ls.pjwt.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.ls.pjwt.utils.constants.Validations;

public record RefreshTokenRequest(@NotBlank(message = Validations.blank) @NotNull(message = Validations.nn) String refreshToken) {
}
