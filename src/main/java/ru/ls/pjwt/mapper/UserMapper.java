package ru.ls.pjwt.mapper;

import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.auth.response.RegisterResponse;
import ru.ls.pjwt.entity.Authority;
import ru.ls.pjwt.entity.User;

import java.util.stream.Collectors;

@Service
public class UserMapper {
    public RegisterResponse userToResponse(User user) {
        return new RegisterResponse(user.getUsername(), user.getEmail(),
                user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet()));
    }
}
