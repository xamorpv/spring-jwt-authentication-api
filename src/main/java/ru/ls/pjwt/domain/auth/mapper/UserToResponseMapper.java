package ru.ls.pjwt.domain.auth.mapper;

import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.user.entity.Authority;
import ru.ls.pjwt.domain.user.entity.User;

@Mapper(
    componentModel = "spring",
    imports = {Collectors.class, Authority.class})
public interface UserToResponseMapper {

  /**
   * Maps a {@link User} entity to a {@link RegisterResponse} DTO.
   *
   * <p>The {@code authorities} field is populated by extracting the authority names from the user's
   * {@link Authority} entities.
   *
   * @param user the user entity to map
   * @return a {@code RegisterResponse} containing the user's username, email, and authorities
   */
  @Mapping(
      target = "authorities",
      expression =
"""
java(
  user.getAuthorities()
  .stream()
  .map(Authority::getName)
  .collect(Collectors.toSet())
)""")
  RegisterResponse userToResponse(User user);
}
