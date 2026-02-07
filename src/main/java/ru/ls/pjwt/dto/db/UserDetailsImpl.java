package ru.ls.pjwt.dto.db;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String username, List<String> authorities) {
        this.username = username;
        this.authorities = authorities == null ? null : authorities.stream().map(GrantedAuthorityImpl::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return null;
    }
}
