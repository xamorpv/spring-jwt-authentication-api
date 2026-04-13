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

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        log.debug("loading user {}...", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException(exceptionsProperties.badCredentials()));
    }

    @Transactional
    public User registerNewUser(CreateUserCommand createUserCommand) {
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
            log.warn("race condition during registration for user {} or email {}. error: {}",
                    createUserCommand.username(), createUserCommand.email(), e.getMessage(), e); // на всякий случай логируем - вдруг это непредвиденная ошибка бд (если добавится какой-то функционал)
            throw new NotUniqueDataException(exceptionsProperties.emailOrUserExists());
        }
    }

    private User saveNewUser(CreateUserCommand createUserCommand) {
        log.debug("saving user {}", createUserCommand.username());
        User user = createUserCommandToUserMapper.commandToUser(createUserCommand,
                passwordEncoder.encode(createUserCommand.rawPassword()));
        authorityService.assignDefaultAuthority(user);
        log.debug("user {} saved", user.getUsername());
        return userRepository.save(user);
    }
}
