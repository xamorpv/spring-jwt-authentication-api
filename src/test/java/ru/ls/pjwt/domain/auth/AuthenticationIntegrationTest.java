package ru.ls.pjwt.domain.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebSecurityTest;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.repository.UserRepository;
import ru.ls.pjwt.helper.user.StandardUser;

public class AuthenticationIntegrationTest extends WebSecurityTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturn401WhenGivenWrongPassword() throws Exception {
        userRegistrationHelper.assertSuccessRegistration();
        userAuthenticationHelper.assertFailureLogin(new LoginRequest(StandardUser.USERNAME, StandardUser.PASSWORD+"WRONG"), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturn401WhenGivenNonExistentUsername() throws Exception {
        userAuthenticationHelper.assertFailureLogin(StandardUser.loginRequest(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturn401WhenGivenLockedAccount() throws Exception {
        userRegistrationHelper.assertSuccessRegistration();
        User user = userRepository.findByUsername(StandardUser.USERNAME).orElseThrow();
        user.setAccountNonLocked(false);
        userRepository.saveAndFlush(user);
        userAuthenticationHelper.assertFailureLogin(StandardUser.loginRequest(), HttpStatus.UNAUTHORIZED.value());
    }
}
