package ru.ls.pjwt.service.auth.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.request.RegisterRequest;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.NotUniqueDataException;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.repository.UserRepository;
import ru.ls.pjwt.utils.LogUtils;
import ru.ls.pjwt.utils.constants.Exceptions;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserSecurity {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

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

        log.debug("status {} success", LogUtils.safeUserDetails(userMapper.userEntityToUserDetails(user)));
    }

    public User validateUsername(String username) {
        log.debug("validating username {}", username);
        User user = userService.loadUser(username);
        log.debug("user loaded {}", LogUtils.safeUserDetails(userMapper.userEntityToUserDetails(user)));
        checkAccountStatus(user);
        return user;
    }
}
