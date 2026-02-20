package ru.ls.pjwt.service.auth.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.repository.UserRepository;
import ru.ls.pjwt.utils.LogUtils;
import ru.ls.pjwt.utils.constants.Exceptions;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public User saveNewUser(String username, String password, String email) {
        log.debug("saving user {}", username);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        authorityService.addUserAuthority(user);
        log.debug("user {} saved", LogUtils.safeUserDetails(userMapper.userEntityToUserDetails(user)));
        return userRepository.save(user);
    }

    public User loadUser(String username) {
        log.debug("loading user {}...", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException(Exceptions.BAD_CREDENTIALS));
    }

    @SuppressWarnings("CallBeanMethodFromSameClass")
    public UserDetails loadUserDetails(String username) {
        return userMapper.userEntityToUserDetails(loadUser(username));
    }
}
