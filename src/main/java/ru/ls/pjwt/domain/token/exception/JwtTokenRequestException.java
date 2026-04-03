package ru.ls.pjwt.domain.token.exception;

public class JwtTokenRequestException extends RuntimeException {
    public JwtTokenRequestException(String message) {
        super(message);
    }
}
