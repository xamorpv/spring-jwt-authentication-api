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
  @Mapping(
      target = "authorities",
      expression =
          "java(user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet()))")
  RegisterResponse userToResponse(User user);
}
