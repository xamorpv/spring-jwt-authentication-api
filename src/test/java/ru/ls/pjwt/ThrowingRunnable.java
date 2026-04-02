package ru.ls.pjwt;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
