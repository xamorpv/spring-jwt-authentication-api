package ru.ls.pjwt.execution;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Component;

/**
 * Utility for running tasks asynchronously and waiting for their completion in integration tests.
 *
 * <p>Useful for simulating concurrent requests (e.g., race conditions) without spawning platform
 * threads manually.
 */
@Component
public class ConcurrentExecutor {
  /**
   * Executes the given runnable in a separate thread and waits at most 10 seconds for its
   * completion.
   *
   * @param runnable the task to execute concurrently
   * @throws RuntimeException if the task throws an exception or the waiting time elapses
   */
  public void runAsyncAndWait(final ThrowingRunnable runnable) {
    final CompletableFuture<Void> future =
        CompletableFuture.runAsync(
            () -> {
              try {
                runnable.run();
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });

    try {
      future.get(10, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  /** A {@link Runnable}-like interface that allows throwing checked exceptions. */
  public interface ThrowingRunnable {
    /**
     * Executes the task, potentially throwing a checked exception.
     *
     * @throws Exception if the task fails
     */
    void run() throws Exception;
  }
}
