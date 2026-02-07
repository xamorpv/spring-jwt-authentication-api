package ru.ls.pjwt.dto.auth.response;


import java.util.Set;

public record RegisterResponse(String username, String email, Set<String> authorities) {
}
