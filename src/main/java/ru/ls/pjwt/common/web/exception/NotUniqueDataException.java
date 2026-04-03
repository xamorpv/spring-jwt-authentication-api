package ru.ls.pjwt.common.web.exception;

public class NotUniqueDataException extends RuntimeException {
    public NotUniqueDataException(String message) {
        super(message);
    }
}
