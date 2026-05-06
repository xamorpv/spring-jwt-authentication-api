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
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void runAsyncAndWait(final ThrowingRunnable runnable) {
    final CompletableFuture<Void> future =
        CompletableFuture.runAsync(
            () -> {
              try {
                runnable.run();
              } catch (Exception e) {
                throw new ConcurrentExecutionException("Task execution failed", e);
              }
            });

    try {
      future.get(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ConcurrentExecutionException("Task interrupted", e);
    } catch (ExecutionException | TimeoutException e) {
      throw new ConcurrentExecutionException("Task failed or timed out", e);
    }
  }
}
