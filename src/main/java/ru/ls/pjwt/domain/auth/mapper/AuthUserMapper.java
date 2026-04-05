package ru.ls.pjwt.domain.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;
import ru.ls.pjwt.domain.user.entity.Authority;
import ru.ls.pjwt.domain.user.entity.User;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class, Authority.class})
public interface AuthUserMapper {


    @Mapping(target = "authorities", expression = "java(user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet()))")
    RegisterResponse userToResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "passwordHash", source = "passwordHash")
    User requestToUser(CreateUserCommand command, String passwordHash);
}
