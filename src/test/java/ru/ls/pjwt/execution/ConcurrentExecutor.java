package ru.ls.pjwt.execution;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class ConcurrentExecutor {
    public void runAsyncAndWait(ThrowingRunnable runnable) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(()-> {
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

    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
