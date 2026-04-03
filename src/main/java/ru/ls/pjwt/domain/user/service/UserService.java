package ru.ls.pjwt.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.common.properties.ExceptionsProperties;
import ru.ls.pjwt.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final ExceptionsProperties exceptionsProperties;
    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User saveNewUser(RegisterRequest registerRequest) {
        log.debug("saving user {}", registerRequest.username());
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setEmail(registerRequest.email());
        authorityService.addUserAuthority(user);
        log.debug("user {} saved", user.getUsername());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User loadUser(String username) {
        log.debug("loading user {}...", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException(exceptionsProperties.badCredentials()));
    }
}
