package ru.ls.pjwt.service.auth.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.utils.constants.Exceptions;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSecurity {
    private final UserService userService;

    public void checkAccountStatus(User user) {
        log.debug("check status {}", user);

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(Exceptions.ACCOUNT_EXPIRED);
        }
        if (!user.isAccountNonLocked()) {
            throw new LockedException(Exceptions.ACCOUNT_LOCKED);
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(Exceptions.CREDENTIALS_EXPIRED);
        }
        if (!user.isEnabled()) {
            throw new DisabledException(Exceptions.ACCOUNT_DISABLED);
        }

        log.debug("status {} success", user.getUsername());
    }

    public User validateUsername(String username) {
        log.debug("validating username {}", username);
        User user = userService.loadUser(username);
        log.debug("user loaded {}", user.getUsername());
        checkAccountStatus(user);
        return user;
    }
}
