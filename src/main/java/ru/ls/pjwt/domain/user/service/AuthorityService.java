package ru.ls.pjwt.domain.user.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.domain.user.entity.Authority;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.common.web.exception.ServerError;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import ru.ls.pjwt.domain.user.repository.AuthorityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthoritiesProperties authoritiesProperties;
    private final AuthorityRepository authorityRepository;
    private Long userAuthorityId;

    @PostConstruct
    private void loadUserAuthority() {
        userAuthorityId = authorityRepository.findByAuthority(authoritiesProperties.user())
                .map(Authority::getId)
                .orElseThrow(() -> new ServerError("authority USER not found!"));
    }

    @Transactional
    public void assignDefaultAuthority(User user) {
        log.debug("add user authority for user with username={}", user.getUsername());

        Authority authority = authorityRepository.getReferenceById(userAuthorityId);
        user.getAuthorities().add(authority);
        log.debug("authority saved for username: {}", user.getUsername());
    }
}
