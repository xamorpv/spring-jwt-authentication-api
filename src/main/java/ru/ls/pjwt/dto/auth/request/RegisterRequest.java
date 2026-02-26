package ru.ls.pjwt.dto.auth.request;

import jakarta.validation.constraints.Email;
import ru.ls.pjwt.utils.constants.Validations;
import ru.ls.pjwt.validation.ValidString;

public record RegisterRequest(
        @ValidString
        String username,
        @ValidString(max = 255) @Email(message = Validations.email)
        String email,
        @ValidString(min = 8, max = 128)
        String password
) {
}
