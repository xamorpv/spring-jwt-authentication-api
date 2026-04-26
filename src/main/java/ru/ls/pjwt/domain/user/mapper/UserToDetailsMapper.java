package ru.ls.pjwt.domain.user.mapper;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.domain.user.entity.User;

@Component
public class UserToDetailsMapper {
  /**
   * Converts a domain {@link User} entity into a Spring Security {@link UserDetails} suitable for
   * authentication and authorization.
   *
   * <p>The password is set to an empty string because authentication is performed via JWT, not by
   * password validation. Account status flags are mapped with inverted meaning to match the {@link
   * UserDetails} contract: {@link UserDetails#isAccountNonExpired()} &rarr; {@code accountExpired}
   * (negated), etc.
   *
   * @param user the user entity to convert
   * @return a fully populated {@link UserDetails} instance ready for the security context
   */
  public UserDetails userEntityToUserDetails(User user) {
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password("")
        .authorities(
            user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList())
        .accountExpired(!user.isAccountNonExpired())
        .accountLocked(!user.isAccountNonLocked())
        .disabled(!user.isEnabled())
        .credentialsExpired(!user.isCredentialsNonExpired())
        .build();
  }
}
