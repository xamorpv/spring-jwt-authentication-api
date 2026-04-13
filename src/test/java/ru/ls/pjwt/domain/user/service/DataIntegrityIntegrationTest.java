package ru.ls.pjwt.domain.user.service;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;

public class DataIntegrityIntegrationTest extends WebSecurityTest {
    @Test
    void shouldReturn409WhenUsernameConflict() throws Exception {
        String notUniqueUsername = "user123456";
        refreshTokenHelper.assertSuccessRegistration(new RegisterRequest(notUniqueUsername, "email_unique_qwerty@gmail.com", "password"));
        refreshTokenHelper.assertFailureRegistration(new RegisterRequest(notUniqueUsername,
                "email_unique_123456@gmail.com", "password"), HttpStatus.SC_CONFLICT);
    }

    @Test
    void shouldReturn409WhenEmailConflict() throws Exception {
        String notUniqueEmail = "not_unique_email@gmail.com";
        refreshTokenHelper.assertSuccessRegistration(new RegisterRequest("unique_username_qwerty", notUniqueEmail, "password"));
        refreshTokenHelper.assertFailureRegistration(new RegisterRequest("unique_username_123456",
                notUniqueEmail, "password"), HttpStatus.SC_CONFLICT);
    }
}
