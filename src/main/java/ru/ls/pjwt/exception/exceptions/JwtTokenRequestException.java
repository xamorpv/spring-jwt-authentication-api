package ru.ls.pjwt.exception.exceptions;

public class JwtTokenRequestException extends RuntimeException {
    public JwtTokenRequestException(String message) {
        super(message);
    }
}
