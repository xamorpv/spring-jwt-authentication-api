package ru.ls.pjwt.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.repository.UserRepository;
import ru.ls.pjwt.utils.LogUtils;
import ru.ls.pjwt.utils.constants.Exceptions;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    public User saveNewUser(String username, String password, String email) {
        log.debug("saving user {}", username);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        User saved = userRepository.save(user); // важно сохранить сейчас - user получит id и в него можно добавлять authorities
        authorityService.addUserAuthority(saved);
        log.debug("user {} saved", LogUtils.safeUserDetails(saved));
        return saved;
    }

    public User loadUser(String username) {
        log.debug("loading user {}...", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException(Exceptions.BAD_CREDENTIALS));
    }
}
