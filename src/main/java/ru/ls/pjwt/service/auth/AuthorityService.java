package ru.ls.pjwt.service.auth;

import jakarta.annotation.PostConstruct;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private volatile Long userAuthorityId;

    @PostConstruct
    public void loadUserAuthority() {
        userAuthorityId = authorityRepository.findByAuthority(Authorities.USER)
                .map(Authority::getId)
                .orElseThrow(()->new ServerError("authority USER not found!"));
    }

    @Transactional
    public void addUserAuthority(User user) {
        log.debug("add user authority for user with id={}, username={}", user.getId(), user.getUsername());

        Authority authority = authorityRepository.getReferenceById(userAuthorityId);
        user.getAuthorities().add(authority);
        log.debug("authority saved for user: {}", LogUtils.safeUserDetails(user));
    }
}
