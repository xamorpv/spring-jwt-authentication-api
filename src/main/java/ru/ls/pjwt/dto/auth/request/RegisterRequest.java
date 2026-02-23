package ru.ls.pjwt.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.ls.pjwt.utils.constants.Validations;

public record RegisterRequest(
        @NotBlank(message = Validations.blank) @Size(min = 3, max = 48, message = Validations.size)
        String username,
        @NotBlank(message = Validations.blank) @Email(message = Validations.email) @Size(min = 3, max = 255, message = Validations.size)
        String email,
        @NotBlank(message = Validations.blank) @Size(min = 8, max = 128, message = Validations.size)
        String password
) {
}
