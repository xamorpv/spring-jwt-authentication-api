package ru.ls.pjwt.helper.user;

import lombok.experimental.UtilityClass;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;

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
