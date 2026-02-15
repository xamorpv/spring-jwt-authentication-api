package ru.ls.pjwt.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.ServerError;
import ru.ls.pjwt.repository.AuthorityRepository;
import ru.ls.pjwt.utils.constants.Roles;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

    public void addUserAuthority(User user) {
        log.debug("add user authority for user with id={}, username={}", user.getUsername(), user.getId());
        Authority authority = authorityRepository.findByName(Roles.USER).orElseThrow(()->new ServerError("authority USER not found!"));
        authority.getUsers().add(user);
        user.getAuthorities().add(authority);
        log.debug("authority saved {}", authority);
    }
}
