package ru.ls.pjwt.execution;

/** A {@link Runnable}-like interface that allows throwing checked exceptions. */
@FunctionalInterface
public interface ThrowingRunnable {
  /**
   * Executes the task, potentially throwing a checked exception.
   *
   * @throws Exception if the task fails
   */
  void run() throws Exception;
}
