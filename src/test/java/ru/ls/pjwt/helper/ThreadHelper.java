package ru.ls.pjwt.helper;

import org.springframework.stereotype.Component;
import ru.ls.pjwt.util.ThrowingRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class ThreadHelper {
    public void runInIndependentThread(ThrowingRunnable runnable) {
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
}
