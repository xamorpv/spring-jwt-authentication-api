package ru.ls.pjwt.domain.token.exception;

public class JwtTokenRequestException extends RuntimeException {
  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message (which is saved for retrieval by the {@link #getMessage()}
   *     method)
   */
  public JwtTokenRequestException(String message) {
    super(message);
  }
}
