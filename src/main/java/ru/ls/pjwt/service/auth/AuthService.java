package ru.ls.pjwt.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.request.RegisterRequest;
import ru.ls.pjwt.dto.auth.response.RegisterResponse;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.NotUniqueDataException;
import ru.ls.pjwt.exception.exceptions.ServerError;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.properties.ExceptionsProperties;
import ru.ls.pjwt.service.auth.user.UserSecurity;
import ru.ls.pjwt.service.auth.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final ExceptionsProperties exceptionsProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserSecurity userSecurity;
    private final UserMapper userMapper;

    public User authenticate(String username, String password) {
        log.debug("auth process started with username: {}", username);
        User user = userService.loadUser(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.debug("wrong password; throwing bad credentials");
            throw new BadCredentialsException(exceptionsProperties.badCredentials());
        }
        log.debug("password exists");
        userSecurity.checkAccountStatus(user);
        log.info("user {} authenticated", username);
        return user;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            log.debug("register: user {}", registerRequest.username());
            return userMapper.userToResponse(userService.saveNewUser(registerRequest));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_username_key")) {
                log.debug("username {} exists", registerRequest.username());
                throw new NotUniqueDataException(exceptionsProperties.userExists());
            } else if (e.getMessage().contains("users_email_key")) {
                log.debug("email {} exists", registerRequest.email());
                throw new NotUniqueDataException(exceptionsProperties.emailExists());
            } else {
                throw new ServerError("DataIntegrityViolationException while saving user: "+e.getMessage());
            }
        }
    }
}
