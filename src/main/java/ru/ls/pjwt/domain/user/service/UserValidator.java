package ru.ls.pjwt.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.user.entity.User;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserValidator {
  private final ExceptionsProperties exceptionsProperties;

  /**
   * Validates the account status of the given user.
   *
   * @param user the user to validate
   * @throws AccountStatusException if the user account is locked, disabled, or expired
   */
  public void validateAccountStatus(final User user) {
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
  }
}
