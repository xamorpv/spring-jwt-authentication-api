package ru.ls.pjwt.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.common.property.ExceptionsProperties;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserValidator {
    private final ExceptionsProperties exceptionsProperties;

    public void validateAccountStatus(User user) {
        log.debug("validating status {}", user);

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
}
