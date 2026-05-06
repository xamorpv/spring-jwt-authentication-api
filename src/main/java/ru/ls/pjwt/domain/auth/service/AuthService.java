package ru.ls.pjwt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.web.exception.NotUniqueDataException;
import ru.ls.pjwt.domain.auth.dto.request.LoginRequest;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.LoginResponse;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.auth.mapper.CommandMapper;
import ru.ls.pjwt.domain.auth.mapper.UserToResponseMapper;
import ru.ls.pjwt.domain.token.dto.TokenPair;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.service.TokenService;
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

  private final TokenService tokenService; // <-- Добавляем зависимость

  /**
   * Authenticates the user and issues a new token pair.
   *
   * <p>Delegates password verification to {@link #authenticate(String, String)} and token
   * generation to {@link TokenService#createTokens(User)}.
   *
   * @param loginRequest login credentials
   * @return generated access and refresh tokens wrapped in {@link LoginResponse}
   * @throws BadCredentialsException if the username does not exist or the password is invalid
   * @throws AccountStatusException if the account is locked, disabled, or expired
   */
  public LoginResponse login(final LoginRequest loginRequest) {
    final User user = authenticate(loginRequest.username(), loginRequest.password());
    final TokenPair tokenPair = tokenService.createTokens(user);
    return new LoginResponse(tokenPair.refreshToken(), tokenPair.accessToken());
  }

  /**
   * Refreshes an access token using the given refresh token.
   *
   * @param refreshToken the raw refresh token string
   * @return new token pair
   * @throws JwtTokenRequestException if the token is invalid, expired, or of the wrong type
   */
  public LoginResponse refresh(final String refreshToken) {
    final TokenPair tokenPair = tokenService.refreshTokens(refreshToken);
    return new LoginResponse(tokenPair.refreshToken(), tokenPair.accessToken());
  }

  /**
   * Invalidates the refresh token, preventing further rotations.
   *
   * @param refreshToken the raw refresh token string
   */
  public void logout(final String refreshToken) {
    tokenService.invalidateRefreshToken(refreshToken);
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

  /**
   * Authenticates a user with the given username and password, and validates the account status.
   *
   * @param username the user's username
   * @param password the raw password to verify
   * @return the authenticated {@link User} entity (with eagerly loaded authorities)
   * @throws BadCredentialsException if the username is not found or the password does not match
   * @throws AccountStatusException if the user account is locked, disabled, or expired
   */
  private User authenticate(final String username, final String password) {
    final User user = userService.findUserByUsername(username);
    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new BadCredentialsException(exceptionsProperties.badCredentials());
    }
    userValidator.validateAccountStatus(user);
    log.info("user {} authenticated", username);
    return user;
  }
}
