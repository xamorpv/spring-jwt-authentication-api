package ru.ls.pjwt.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;
import ru.ls.pjwt.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface CreateUserCommandToUserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "passwordHash", source = "passwordHash")
    User commandToUser(CreateUserCommand command, String passwordHash);
}
