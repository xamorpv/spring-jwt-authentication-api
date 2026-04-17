package ru.ls.pjwt.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import ru.ls.pjwt.common.web.validation.ValidString;
import ru.ls.pjwt.common.web.validation.ValidationConstants;

public record RegisterRequest(
        @ValidString
        String username,
        @ValidString(max = ValidationConstants.MAX_EMAIL_LENGTH) @Email(message = "{validation.email}")
        String email,
        @ValidString(min = ValidationConstants.MIN_PASSWORD_LENGTH, max = ValidationConstants.MAX_PASSWORD_LENGTH)
        String rawPassword
) {
}
