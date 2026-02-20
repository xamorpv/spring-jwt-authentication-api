package ru.ls.pjwt.service.auth;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.request.RegisterRequest;
import ru.ls.pjwt.dto.auth.response.RegisterResponse;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.service.auth.user.UserSecurity;
import ru.ls.pjwt.service.auth.user.UserService;
import ru.ls.pjwt.utils.constants.Exceptions;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserSecurity userSecurity;
    private final UserMapper userMapper;

    public UserDetails authenticate(String username, String password) {
        log.debug("auth process started with username: {}", username);
        UserDetails userDetails = userService.loadUserDetails(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.debug("wrong password; throwing bad credentials");
            throw new BadCredentialsException(Exceptions.BAD_CREDENTIALS);
        }
        log.debug("password exists");
        userSecurity.checkAccountStatus(userDetails);
        log.info("user {} authenticated", username);
        return userDetails;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        userSecurity.checkExists(registerRequest);
        log.debug("register: user {}", registerRequest.username());
        return userMapper.userToResponse(userService.saveNewUser(registerRequest.username(), registerRequest.password(), registerRequest.email()));
    }
}
