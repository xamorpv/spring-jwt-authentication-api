package ru.ls.pjwt.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.ServerError;
import ru.ls.pjwt.repository.AuthorityRepository;
import ru.ls.pjwt.utils.LogUtils;
import ru.ls.pjwt.utils.constants.Authorities;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private volatile Authority authority;
    private final Object lock = new Object[0];

    public void addUserAuthority(User user) {
        log.debug("add user authority for user with id={}, username={}", user.getId(), user.getUsername());

        if (authority == null) {
            synchronized (lock) {
                if (authority == null) {
                    authority = authorityRepository.findByName(Authorities.USER).orElseThrow(()->new ServerError("authority USER not found!"));
                }
            }
        }

        authority.getUsers().add(user);
        user.getAuthorities().add(authority);
        log.debug("authority saved for user: {}", LogUtils.safeUserDetails(user));
    }
}
