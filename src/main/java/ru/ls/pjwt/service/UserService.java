package ru.ls.pjwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.RegisterRequest;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.NotUniqueDataException;
import ru.ls.pjwt.repository.UserRepository;
import ru.ls.pjwt.utils.constants.Exceptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    public void checkExists(RegisterRequest request) {
        log.debug("check exists username {}", request.username());
        if (userRepository.existsByUsername(request.username())) {
            log.debug("username {} exists", request.username());
            throw new NotUniqueDataException(Exceptions.USER_EXISTS);
        }
        if (userRepository.existsByEmail(request.email())) {
            log.debug("email {}  exists", request.username());
            throw new NotUniqueDataException(Exceptions.EMAIL_EXISTS);
        }
    }

    public void checkAccountStatus(UserDetails userDetails) {
        log.debug("check status {}", userDetails);

        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException(Exceptions.ACCOUNT_EXPIRED);
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException(Exceptions.ACCOUNT_LOCKED);
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(Exceptions.CREDENTIALS_EXPIRED);
        }
        if (!userDetails.isEnabled()) {
            throw new DisabledException(Exceptions.ACCOUNT_DISABLED);
        }

        log.debug("status {} success", userDetails);
    }

    public UserDetails saveNewUser(String username, String password, String email) {
        log.debug("saving user {}", username);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        authorityService.addUserAuthority(user);
        User saved = userRepository.save(user);
        log.debug("user {} saved", user);
        return saved;
    }
}
