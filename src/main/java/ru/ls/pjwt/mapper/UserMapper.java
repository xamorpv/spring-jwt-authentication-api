package ru.ls.pjwt.mapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.dto.auth.response.RegisterResponse;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public RegisterResponse userToResponse(User user) {
        return new RegisterResponse(user.getUsername(), user.getEmail(),
                user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet()));
    }

    public UserDetails userEntityToUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password("")
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .disabled(!user.isEnabled())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .build();
    }
}
