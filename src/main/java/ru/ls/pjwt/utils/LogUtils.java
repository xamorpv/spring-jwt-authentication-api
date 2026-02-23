package ru.ls.pjwt.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@UtilityClass
public class LogUtils {
    public String safeUserDetails(UserDetails userDetails) {
        return safeUserDetails(userDetails, true);
    }

    public String safeUserDetails(UserDetails userDetails, boolean isDebug) {
        if (!log.isDebugEnabled() && isDebug) {
            return "[debug disabled]";
        }
        if (userDetails == null) {
            return "null";
        }
        return String.format("UserDetails{username=%s, authorities=%s, accountNonExpired=%s, accountNonLocked=%s, credentialsNonExpired=%s, enabled=%s}",
                userDetails.getUsername(), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                userDetails.isAccountNonExpired(), userDetails.isAccountNonLocked(), userDetails.isCredentialsNonExpired(), userDetails.isEnabled());
    }
}
