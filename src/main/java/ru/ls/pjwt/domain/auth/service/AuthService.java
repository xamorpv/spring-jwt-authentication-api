package ru.ls.pjwt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.common.web.exception.NotUniqueDataException;
import ru.ls.pjwt.domain.user.mapper.UserMapper;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.user.repository.UserRepository;
import ru.ls.pjwt.domain.user.service.UserValidator;
import ru.ls.pjwt.domain.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final ExceptionsProperties exceptionsProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public User authenticate(String username, String password) {
        log.debug("auth process started with username: {}", username);
        User user = userService.loadUser(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.debug("wrong password; throwing bad credentials");
            throw new BadCredentialsException(exceptionsProperties.badCredentials());
        }
        log.debug("password exists");
        userValidator.checkAccountStatus(user);
        log.info("user {} authenticated", username);
        return user;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new NotUniqueDataException(exceptionsProperties.emailExists());
        }
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new NotUniqueDataException(exceptionsProperties.userExists());
        }

        try {
            log.debug("register: user {}", registerRequest.username());
            return userMapper.userToResponse(userService.saveNewUser(registerRequest));
        } catch (DataIntegrityViolationException e) {
            log.warn("race condition during registration for user {} or email {}. error: {}",
                    registerRequest.username(), registerRequest.email(), e.getMessage(), e); // на всякий случай логируем - вдруг это непредвиденная ошибка бд (если добавится какой-то функционал)
            throw new NotUniqueDataException(exceptionsProperties.emailOrUserExists());
        }
    }
}
