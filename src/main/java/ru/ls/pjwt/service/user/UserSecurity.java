package ru.ls.pjwt.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.properties.ExceptionsProperties;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSecurity {
    private final ExceptionsProperties exceptionsProperties;
    private final UserService userService;

    public void checkAccountStatus(User user) {
        log.debug("check status {}", user);

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(exceptionsProperties.accountExpired());
        }
        if (!user.isAccountNonLocked()) {
            throw new LockedException(exceptionsProperties.accountLocked());
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(exceptionsProperties.credentialsExpired());
        }
        if (!user.isEnabled()) {
            throw new DisabledException(exceptionsProperties.accountDisabled());
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
