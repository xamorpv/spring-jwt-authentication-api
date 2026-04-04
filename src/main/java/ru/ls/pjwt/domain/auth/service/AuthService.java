package ru.ls.pjwt.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.auth.mapper.CommandMapper;
import ru.ls.pjwt.domain.auth.mapper.AuthUserMapper;
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
    private final AuthUserMapper authUserMapper;
    private final CommandMapper commandMapper;

    public User authenticate(String username, String password) {
        User user = userService.findUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException(exceptionsProperties.badCredentials());
        }
        userValidator.validateAccountStatus(user);
        log.info("user {} authenticated", username);
        return user;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authUserMapper.userToResponse(
                userService.registerNewUser(commandMapper.registerRequestToCommand(registerRequest))
        );
        log.info("user registered successfully: {}", registerResponse);
        return registerResponse;
    }
}
