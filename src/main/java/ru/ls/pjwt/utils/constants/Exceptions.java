package ru.ls.pjwt.utils.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Exceptions {
    public final String BAD_CREDENTIALS = "BAD_CREDENTIALS", ACCOUNT_EXPIRED = "ACCOUNT_EXPIRED", ACCOUNT_LOCKED = "ACCOUNT_LOCKED",
            CREDENTIALS_EXPIRED = "CREDENTIALS_EXPIRED", ACCOUNT_DISABLED = "ACCOUNT_DISABLED", USER_EXISTS = "USER_ALREADY_EXISTS",
            EMAIL_EXISTS = "EMAIL_ALREADY_EXISTS", VALIDATION = "VALIDATION_FAILED",
            REFRESH_TOKEN_COMPROMISED = "refresh token was compromised. you may be get hacked. please re-login";
}
