package ru.ls.pjwt.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;
import ru.ls.pjwt.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface CreateUserCommandToUserMapper {
  /**
   * Maps a {@link CreateUserCommand} and a separately provided encoded password into a new {@link
   * User} entity.
   *
   * <p>The method populates all basic fields required for a new user account:
   *
   * <ul>
   *   <li>username and email are taken from the command
   *   <li>the password hash is assigned from the second parameter
   *   <li>account status flags (non‑expired, non‑locked, credentials non‑expired, enabled) are all
   *       set to {@code true}
   *   <li>{@code id} and {@code authorities} are ignored (they are managed separately)
   * </ul>
   *
   * @param command the registration command with username, email and raw password
   * @param passwordHash the encoded password to store in the user entity
   * @return a new {@link User} entity ready to be persisted (not yet saved)
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "accountNonExpired", constant = BooleanConstants.TRUE)
  @Mapping(target = "accountNonLocked", constant = BooleanConstants.TRUE)
  @Mapping(target = "credentialsNonExpired", constant = BooleanConstants.TRUE)
  @Mapping(target = "enabled", constant = BooleanConstants.TRUE)
  @Mapping(target = "passwordHash", source = "passwordHash")
  User commandToUser(CreateUserCommand command, String passwordHash);
}
