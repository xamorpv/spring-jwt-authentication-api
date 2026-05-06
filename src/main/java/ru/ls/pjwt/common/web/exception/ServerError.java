package ru.ls.pjwt.common.web.exception;

/**
 * Thrown to indicate an unexpected internal server error. This typically wraps an unrecoverable
 * condition that should be logged and result in a 500 response.
 */
public class ServerError extends RuntimeException {
  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public ServerError(final String message) {
    super(message);
  }
}
