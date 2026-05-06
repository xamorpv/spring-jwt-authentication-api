package ru.ls.pjwt.domain.auth.dto.response;

import java.util.Set;

public record RegisterResponse(String username, String email, Set<String> authorities) {}
