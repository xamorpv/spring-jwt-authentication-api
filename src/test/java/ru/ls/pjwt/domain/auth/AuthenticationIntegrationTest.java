package ru.ls.pjwt.domain.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.repository.UserRepository;
import ru.ls.pjwt.fixture.StandardUserFixture;
import ru.ls.pjwt.steps.user.AuthenticationSteps;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Import({RegistrationSteps.class, AuthenticationSteps.class})
public class AuthenticationIntegrationTest extends WebIntegrationTest {
  @Autowired private RegistrationSteps registrationSteps;

  @Autowired private AuthenticationSteps authenticationSteps;

  @Autowired private UserRepository userRepository;

  @Test
  void shouldReturn401WhenGivenWrongPassword() throws Exception {
    registrationSteps.registerSuccessfully();
    authenticationSteps.expectLoginFailure(
        new LoginRequest(
            StandardUserFixture.DEFAULT_USERNAME, StandardUserFixture.DEFAULT_PASSWORD + "WRONG"),
        HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void shouldReturn401WhenGivenNonExistentUsername() throws Exception {
    authenticationSteps.expectLoginFailure(
        StandardUserFixture.getDefaultLoginRequest(), HttpStatus.UNAUTHORIZED.value());
  }

  @Test
  void shouldReturn401WhenGivenLockedAccount() throws Exception {
    registrationSteps.registerSuccessfully();
    final User user =
        userRepository.findByUsername(StandardUserFixture.DEFAULT_USERNAME).orElseThrow();
    user.setAccountNonLocked(false);
    userRepository.saveAndFlush(user);
    authenticationSteps.expectLoginFailure(
        StandardUserFixture.getDefaultLoginRequest(), HttpStatus.UNAUTHORIZED.value());
  }
}
