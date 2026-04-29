package ru.ls.pjwt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.web.exception.NotUniqueDataException;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.auth.mapper.CommandMapper;
import ru.ls.pjwt.domain.auth.mapper.UserToResponseMapper;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.service.UserService;
import ru.ls.pjwt.domain.user.service.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
  private final ExceptionsProperties exceptionsProperties;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final UserValidator userValidator;
  private final UserToResponseMapper userToResponseMapper;
  private final CommandMapper commandMapper;

  /**
   * Authenticates a user with the given username and password, and validates the account status.
   *
   * @param username the user's username
   * @param password the raw password to verify
   * @return the authenticated {@link User} entity (with eagerly loaded authorities)
   * @throws BadCredentialsException if the username is not found or the password does not match
   * @throws AccountStatusException if the user account is locked, disabled, or expired
   */
  public User authenticate(final String username, final String password) {
    final User user = userService.findUserByUsername(username);
    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new BadCredentialsException(exceptionsProperties.badCredentials());
    }
    userValidator.validateAccountStatus(user);
    log.info("user {} authenticated", username);
    return user;
  }

  /**
   * Registers a new user from the provided registration request.
   *
   * @param registerRequest the registration details (username, email, raw password)
   * @return a {@link RegisterResponse} with the registered user's information
   * @throws NotUniqueDataException if the username or email already exists, including due to a
   *     concurrent registration conflict
   */
  public RegisterResponse register(final RegisterRequest registerRequest) {
    final RegisterResponse registerResponse =
        userToResponseMapper.userToResponse(
            userService.registerNewUser(commandMapper.registerRequestToCommand(registerRequest)));
    log.info("user registered successfully: {}", registerResponse);
    return registerResponse;
  }
}
