package ru.ls.pjwt.domain.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationEnvironment;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.fixture.StandardUserFixture;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Import(RegistrationSteps.class)
class DataIntegrityIntegrationTest extends WebIntegrationEnvironment {
  @Autowired private RegistrationSteps registrationSteps;

  @Test
  void shouldReturn409WhenUsernameConflict() throws Exception {
    final String notUniqueUsername = "user123456";
    registrationSteps.registerSuccessfully(
        new RegisterRequest(
            notUniqueUsername,
            "email_unique_qwerty@gmail.com",
            StandardUserFixture.DEFAULT_PASSWORD));
    registrationSteps.expectRegistrationFailure(
        new RegisterRequest(
            notUniqueUsername,
            "email_unique_123456@gmail.com",
            StandardUserFixture.DEFAULT_PASSWORD),
        HttpStatus.CONFLICT.value());
  }

  @Test
  void shouldReturn409WhenEmailConflict() throws Exception {
    final String notUniqueEmail = "not_unique_email@gmail.com";
    registrationSteps.registerSuccessfully(
        new RegisterRequest(
            "unique_username_qwerty", notUniqueEmail, StandardUserFixture.DEFAULT_PASSWORD));
    registrationSteps.expectRegistrationFailure(
        new RegisterRequest(
            "unique_username_123456", notUniqueEmail, StandardUserFixture.DEFAULT_PASSWORD),
        HttpStatus.CONFLICT.value());
  }
}
