package ru.ls.pjwt.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.request.RegisterRequest;
import ru.ls.pjwt.exception.exceptions.NotUniqueDataException;
import ru.ls.pjwt.repository.UserRepository;
import ru.ls.pjwt.utils.LogUtils;
import ru.ls.pjwt.utils.constants.Exceptions;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSecurity {
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public void checkExists(RegisterRequest request) {
        log.debug("check exists username {}", request.username());
        if (userRepository.existsByUsername(request.username())) {
            log.debug("username {} exists", request.username());
            throw new NotUniqueDataException(Exceptions.USER_EXISTS);
        }
        if (userRepository.existsByEmail(request.email())) {
            log.debug("email {} exists", request.email());
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

        log.debug("status {} success", LogUtils.safeUserDetails(userDetails));
    }

    public UserDetails validateUsername(String username) {
        log.debug("validating username {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.debug("user loaded {}", LogUtils.safeUserDetails(userDetails));
        checkAccountStatus(userDetails);
        return userDetails;
    }
}
