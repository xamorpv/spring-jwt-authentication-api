package ru.ls.pjwt.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userService.loadUser(username);
    }
}
