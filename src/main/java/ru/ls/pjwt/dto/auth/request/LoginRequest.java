package ru.ls.pjwt.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import ru.ls.pjwt.utils.constants.Validations;

public record LoginRequest(@NotBlank(message = Validations.blank) String username, @NotBlank(message = Validations.blank) String password) {
}
