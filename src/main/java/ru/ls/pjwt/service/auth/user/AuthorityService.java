package ru.ls.pjwt.service.auth.user;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.exception.exceptions.ServerError;
import ru.ls.pjwt.properties.AuthoritiesProperties;
import ru.ls.pjwt.repository.AuthorityRepository;

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
    public void addUserAuthority(User user) {
        log.debug("add user authority for user with id={}, username={}", user.getId(), user.getUsername());

        Authority authority = authorityRepository.getReferenceById(userAuthorityId);
        user.getAuthorities().add(authority);
        log.debug("authority saved for username: {}", user.getUsername());
    }
}
