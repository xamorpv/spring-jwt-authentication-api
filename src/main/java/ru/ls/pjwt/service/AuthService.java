package ru.ls.pjwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.RegisterRequest;
import ru.ls.pjwt.utils.constants.Exceptions;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserDetails authenticate(String username, String password) {
        log.debug("auth process started with username: {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.debug("wrong password; throwing bad credentials");
            throw new BadCredentialsException(Exceptions.BAD_CREDENTIALS);
        }
        log.debug("password exists");
        userService.checkAccountStatus(userDetails);
        log.info("user {} authenticated", username);
        return userDetails;
    }

    public void validateUsername(String username) {
        log.debug("validating username {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.debug("user loaded {}", userDetails);
        userService.checkAccountStatus(userDetails);
    }

    public UserDetails register(RegisterRequest registerRequest) {
        userService.checkExists(registerRequest);
        log.debug("register: user {}", registerRequest.username());
        return userService.save(registerRequest.username(), registerRequest.password(), registerRequest.email());
    }
}
