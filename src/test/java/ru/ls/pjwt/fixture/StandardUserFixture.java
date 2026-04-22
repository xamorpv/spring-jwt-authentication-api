package ru.ls.pjwt.fixture;

import lombok.experimental.UtilityClass;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;

@UtilityClass
public class StandardUserFixture {
    public final String DEFAULT_USERNAME = "test", DEFAULT_PASSWORD = "test1234567890", DEFAULT_EMAIL = "test@example.com";

    public RegisterRequest getDefaultRegisterRequest() {
        return new RegisterRequest(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public LoginRequest getDefaultLoginRequest() {
        return new LoginRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}
