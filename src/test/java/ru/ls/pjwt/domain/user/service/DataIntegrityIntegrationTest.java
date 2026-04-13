package ru.ls.pjwt.domain.user.service;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;

public class DataIntegrityIntegrationTest extends WebSecurityTest {
    @Test
    void shouldReturn409WhenUsernameConflict() throws Exception {
        String notUniqueUsername = "user123456";
        userRegistrationHelper.assertSuccessRegistration(new RegisterRequest(notUniqueUsername, "email_unique_qwerty@gmail.com", "password"));
        userRegistrationHelper.assertFailureRegistration(new RegisterRequest(notUniqueUsername,
                "email_unique_123456@gmail.com", "password"), HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturn409WhenEmailConflict() throws Exception {
        String notUniqueEmail = "not_unique_email@gmail.com";
        userRegistrationHelper.assertSuccessRegistration(new RegisterRequest("unique_username_qwerty", notUniqueEmail, "password"));
        userRegistrationHelper.assertFailureRegistration(new RegisterRequest("unique_username_123456",
                notUniqueEmail, "password"), HttpStatus.CONFLICT.value());
    }
}
