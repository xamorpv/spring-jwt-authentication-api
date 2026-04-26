package ru.ls.pjwt.common.web.exception;

/**
 * Thrown when an attempt is made to register or update a user with data that violates uniqueness
 * constraints (e.g., username or email).
 */
public class NotUniqueDataException extends RuntimeException {
  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public NotUniqueDataException(String message) {
    super(message);
  }
}
