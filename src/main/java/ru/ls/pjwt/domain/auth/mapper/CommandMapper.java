package ru.ls.pjwt.domain.auth.mapper;

import org.mapstruct.Mapper;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;

@Mapper(componentModel = "spring")
public interface CommandMapper {
  /**
   * Maps a {@link RegisterRequest} to a {@link CreateUserCommand}.
   *
   * @param registerRequest the registration request to map
   * @return the corresponding {@code CreateUserCommand}
   */
  CreateUserCommand registerRequestToCommand(RegisterRequest registerRequest);
}
