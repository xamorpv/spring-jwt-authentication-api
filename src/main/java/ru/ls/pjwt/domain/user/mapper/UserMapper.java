package ru.ls.pjwt.domain.user.mapper;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.domain.user.entity.User;

@Component
public class UserMapper {
    public UserDetails userEntityToUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password("")
                .authorities(user.getAuthorities().stream().map(authority ->
                        new SimpleGrantedAuthority(authority.getAuthority())).toList())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .disabled(!user.isEnabled())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .build();
    }
}
