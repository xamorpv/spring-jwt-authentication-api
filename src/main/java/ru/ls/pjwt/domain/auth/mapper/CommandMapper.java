package ru.ls.pjwt.domain.auth.mapper;

import org.springframework.stereotype.Component;
import ru.ls.pjwt.domain.auth.dto.request.RegisterRequest;
import ru.ls.pjwt.domain.user.dto.CreateUserCommand;

@Component
public class CommandMapper {
    public CreateUserCommand registerRequestToCommand(RegisterRequest registerRequest) {
        return new CreateUserCommand(registerRequest.username(), registerRequest.email(), registerRequest.rawPassword());
    }
}
