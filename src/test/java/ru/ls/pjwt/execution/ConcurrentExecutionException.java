package ru.ls.pjwt.execution;

public class ConcurrentExecutionException extends RuntimeException {
  public ConcurrentExecutionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
