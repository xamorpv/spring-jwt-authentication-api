package ru.ls.pjwt.exception.exceptions;

public class ServerError extends RuntimeException {
    public ServerError(String message) {
        super(message);
    }
}
