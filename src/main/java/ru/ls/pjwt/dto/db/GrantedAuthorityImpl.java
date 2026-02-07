package ru.ls.pjwt.dto.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@ToString
@RequiredArgsConstructor
public class GrantedAuthorityImpl implements GrantedAuthority {
    @Getter
    private final String authority;
}
