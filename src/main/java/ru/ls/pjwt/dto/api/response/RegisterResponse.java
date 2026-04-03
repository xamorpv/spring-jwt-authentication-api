package ru.ls.pjwt.dto.api.response;


import java.util.Set;

public record RegisterResponse(String username, String email, Set<String> authorities) {
}
