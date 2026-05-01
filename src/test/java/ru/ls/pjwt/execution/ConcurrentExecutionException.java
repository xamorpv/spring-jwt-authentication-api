package ru.ls.pjwt.execution;

/**
 * Thrown when a task executed asynchronously in the {@link ConcurrentExecutor} fails, times out, or
 * is interrupted. This exception wraps the original cause for easier debugging in integration
 * tests.
 */
public class ConcurrentExecutionException extends RuntimeException {
  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public ConcurrentExecutionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
