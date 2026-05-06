package ru.ls.pjwt.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.common.web.exception.NotUniqueDataException;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.mapper.CreateUserCommandToUserMapper;
import ru.ls.pjwt.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final ExceptionsProperties exceptionsProperties;
  private final UserRepository userRepository;
  private final AuthorityService authorityService;
  private final PasswordEncoder passwordEncoder;
  private final CreateUserCommandToUserMapper createUserCommandToUserMapper;

  /**
   * Finds a user by username.
   *
   * @param username the username to search for
   * @return the matching {@link User} entity with authorities eagerly loaded
   * @throws BadCredentialsException if no user with the given username exists
   */
  @Transactional(readOnly = true)
  public User findUserByUsername(final String username) {
    log.debug("loading user {}...", username);
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new BadCredentialsException(exceptionsProperties.badCredentials()));
  }

  /**
   * Registers a new user if the username and email are unique.
   *
   * @param createUserCommand the registration data
   * @return the newly persisted {@link User} entity
   * @throws NotUniqueDataException if the username or email already exists, including in the case
   *     of a concurrent registration conflict
   */
  @SuppressWarnings("PMD.PreserveStackTrace")
  @Transactional
  public User registerNewUser(final CreateUserCommand createUserCommand) {
    if (userRepository.existsByEmail(createUserCommand.email())) {
      throw new NotUniqueDataException(exceptionsProperties.emailExists());
    }
    if (userRepository.existsByUsername(createUserCommand.username())) {
      throw new NotUniqueDataException(exceptionsProperties.userExists());
    }

    try {
      log.debug("register: user {}", createUserCommand.username());
      return saveNewUser(createUserCommand);
    } catch (DataIntegrityViolationException e) {
      log.warn(
          "race condition during registration for user {} or email {}. error: {}",
          createUserCommand.username(),
          createUserCommand.email(),
          e.getMessage(),
          e); // на всякий случай логируем - вдруг это непредвиденная ошибка бд (если добавится
      // какой-то функционал)
      throw new NotUniqueDataException(exceptionsProperties.emailOrUserExists());
    }
  }

  private User saveNewUser(final CreateUserCommand createUserCommand) {
    log.debug("saving user {}", createUserCommand.username());
    final String password = passwordEncoder.encode(createUserCommand.rawPassword());
    if (password == null) {
      throw new BadCredentialsException(exceptionsProperties.badCredentials());
    }
    final User user = createUserCommandToUserMapper.commandToUser(createUserCommand, password);
    authorityService.assignDefaultAuthority(user);
    log.debug("user {} saved", user.getUsername());
    return userRepository.save(user);
  }
}
