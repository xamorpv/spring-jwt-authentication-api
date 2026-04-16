package ru.ls.pjwt.common.web.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public final int DEFAULT_MIN_STRING_LENGTH = 3, DEFAULT_MAX_STRING_LENGTH = 48;
    public final int MIN_PASSWORD_LENGTH = 8, MAX_PASSWORD_LENGTH = 128;
    public final int MIN_EMAIL_LENGTH = 255;
}
