package ru.ls.pjwt.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.web.exception.NotUniqueDataException;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.auth.mapper.UserMapper;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final ExceptionsProperties exceptionsProperties;
    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public User saveNewUser(RegisterRequest registerRequest) {
        log.debug("saving user {}", registerRequest.username());
        User user = userMapper.requestToUser(registerRequest, passwordEncoder.encode(registerRequest.password()));
        authorityService.assignDefaultRole(user);
        log.debug("user {} saved", user.getUsername());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        log.debug("loading user {}...", username);
        return userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException(exceptionsProperties.badCredentials()));
    }

    @Transactional
    public User registerNewUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new NotUniqueDataException(exceptionsProperties.emailExists());
        }
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new NotUniqueDataException(exceptionsProperties.userExists());
        }

        try {
            log.debug("register: user {}", registerRequest.username());
            return saveNewUser(registerRequest);
        } catch (DataIntegrityViolationException e) {
            log.warn("race condition during registration for user {} or email {}. error: {}",
                    registerRequest.username(), registerRequest.email(), e.getMessage(), e); // на всякий случай логируем - вдруг это непредвиденная ошибка бд (если добавится какой-то функционал)
            throw new NotUniqueDataException(exceptionsProperties.emailOrUserExists());
        }
    }
}
