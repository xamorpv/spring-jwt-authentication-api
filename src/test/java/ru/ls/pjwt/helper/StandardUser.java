package ru.ls.pjwt.helper;

import lombok.experimental.UtilityClass;
import ru.ls.pjwt.dto.auth.request.LoginRequest;
import ru.ls.pjwt.dto.auth.request.RegisterRequest;

@UtilityClass
public class StandardUser {
    public final String USERNAME = "test", PASSWORD = "test1234567890", EMAIL = "test@example.com";

    public RegisterRequest registerRequest() {
        return new RegisterRequest(USERNAME, EMAIL, PASSWORD);
    }

    public LoginRequest loginRequest() {
        return new LoginRequest(USERNAME, PASSWORD);
    }
}
