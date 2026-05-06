package ru.ls.pjwt.domain.user.dto;

public record CreateUserCommand(String username, String email, String rawPassword) {}
