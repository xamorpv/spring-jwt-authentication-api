package ru.ls.pjwt.domain.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.user.entity.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class UserValidatorUnitTest {
    private final ExceptionsProperties exceptionsProperties = mock(ExceptionsProperties.class);
    private final UserValidator userValidator = new UserValidator(exceptionsProperties);
    private final User user = new User();

    @Test
    void shouldThrowAccountExpiredExceptionWhenUserAccountExpired() {
        user.setAccountNonExpired(false);
        assertThrows(AccountExpiredException.class, ()->userValidator.validateAccountStatus(user));
    }

    @Test
    void shouldThrowLockedExceptionWhenUserAccountLocked() {
        user.setAccountNonLocked(false);
        assertThrows(LockedException.class, ()->userValidator.validateAccountStatus(user));
    }

    @Test
    void shouldThrowCredentialsExpiredExceptionWhenUserCredentialsExpired() {
        user.setCredentialsNonExpired(false);
        assertThrows(CredentialsExpiredException.class, ()->userValidator.validateAccountStatus(user));
    }

    @Test
    void shouldThrowDisabledExceptionWhenUserDisabled() {
        user.setEnabled(false);
        assertThrows(DisabledException.class, ()->userValidator.validateAccountStatus(user));
    }
}
