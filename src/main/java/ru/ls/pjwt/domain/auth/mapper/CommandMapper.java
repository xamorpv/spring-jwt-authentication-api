package ru.ls.pjwt.domain.auth.mapper;

import org.mapstruct.Mapper;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;

@Mapper(componentModel = "spring")
public interface CommandMapper {
  CreateUserCommand registerRequestToCommand(RegisterRequest registerRequest);
}
