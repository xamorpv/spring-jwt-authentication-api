package ru.ls.pjwt.domain.auth;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.common.web.dto.api.ErrorResponse;
import ru.ls.pjwt.common.web.dto.exception.FieldErrorDto;
import ru.ls.pjwt.common.web.validation.ValidationConstants;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.steps.user.RegistrationSteps;

@Slf4j
@Import(RegistrationSteps.class)
class ValidationIntegrationTest extends WebIntegrationTest {
  @Autowired private RegistrationSteps registrationSteps;

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  @DisplayName("Complex validation test for RegisterRequest")
  @Test
  void shouldReturn400andErrorsListWhenRegisterRequestInvalid() throws Exception {
    final List<FieldErrorDto> errors = sendInvalidRequest("sh", "invalidEmail", "short");
    assertNotNull(errors, "errors list");
    assertEquals(3, errors.size(), "errors size");

    assertAll(
        () -> expectResponseContainsField(errors, "username", 1),
        () -> expectResponseContainsField(errors, "email", 1),
        () -> expectResponseContainsField(errors, "rawPassword", 1));
  }

  @ParameterizedTest
  @MethodSource("getInvalidUsernames")
  void shouldReturn400andErrorsListWhenUsernameIsInvalid(final String username, final long times)
      throws Exception {
    final List<FieldErrorDto> errors =
        sendInvalidRequest(username, "valid_email@exaple.com", "validPassword");

    assertEquals(times, errors.size(), "errors size");
    expectResponseContainsField(errors, "username", times);
  }

  @ParameterizedTest
  @MethodSource("getInvalidPasswords")
  void shouldReturn400andErrorsListWhenPasswordIsInvalid(final String password, final long times)
      throws Exception {
    final List<FieldErrorDto> errors =
        sendInvalidRequest("validUsername", "valid_email@example.com", password);

    assertEquals(times, errors.size(), "errors size");
    expectResponseContainsField(errors, "rawPassword", times);
  }

  @ParameterizedTest
  @MethodSource("getInvalidEmails")
  void shouldReturn400andErrorsListWhenEmailIsInvalid(final String email, final long times)
      throws Exception {
    final List<FieldErrorDto> errors = sendInvalidRequest("validUsername", email, "validPassword");

    assertEquals(times, errors.size(), "errors size");
    expectResponseContainsField(errors, "email", times);
  }

  private List<FieldErrorDto> sendInvalidRequest(
      final String username, final String email, final String password) throws Exception {
    final ErrorResponse errorResponse =
        registrationSteps
            .expectRegistrationFailure(
                new RegisterRequest(username, email, password), HttpStatus.BAD_REQUEST.value())
            .data();

    assertNotNull(errorResponse, "error response");
    assertNotNull(errorResponse.errors(), "invalid request should contain errors list");

    log.info("errors list: {}", errorResponse.errors());

    return errorResponse.errors();
  }

  private void expectResponseContainsField(
      final List<FieldErrorDto> errors, final String fieldName, final long times) {
    assertEquals(
        errors.stream().filter(f -> f.field().equals(fieldName)).count(),
        times,
        "error fields containing fieldName: " + fieldName);
  }

  private static Stream<Arguments> getInvalidUsernames() {
    return Stream.of(
        Arguments.of("", 2),
        Arguments.of("1".repeat(ValidationConstants.DEFAULT_MAX_STRING_LENGTH + 1), 1),
        Arguments.of("1".repeat(ValidationConstants.DEFAULT_MIN_STRING_LENGTH - 1), 1));
  }

  private static Stream<Arguments> getInvalidPasswords() {
    return Stream.of(
        Arguments.of("", 2),
        Arguments.of("1".repeat(ValidationConstants.MIN_PASSWORD_LENGTH - 1), 1),
        Arguments.of("1".repeat(ValidationConstants.MAX_PASSWORD_LENGTH + 1), 1));
  }

  private static Stream<Arguments> getInvalidEmails() {
    return Stream.of(
        Arguments.of("", 2),
        Arguments.of("1".repeat(ValidationConstants.MAX_EMAIL_LENGTH + 1), 2),
        Arguments.of("invalidFormat", 1),
        Arguments.of("invalid-characters", 1));
  }
}
