package ru.ls.pjwt.exception.exceptions;

public class RefreshTokenCompromiseException extends RuntimeException {
    public RefreshTokenCompromiseException() {
        super("refresh token was compromised. you may be get hacked. please re-login");
    }
}
