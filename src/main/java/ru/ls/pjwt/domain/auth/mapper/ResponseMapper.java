package ru.ls.pjwt.domain.auth.mapper;

import org.springframework.stereotype.Component;
import ru.ls.pjwt.domain.auth.dto.response.RegisterResponse;
import ru.ls.pjwt.domain.user.entity.Authority;
import ru.ls.pjwt.domain.user.entity.User;

import java.util.stream.Collectors;

@Component
public class ResponseMapper {
    public RegisterResponse userToResponse(User user) {
        return new RegisterResponse(user.getUsername(), user.getEmail(),
                user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet()));
    }
}
