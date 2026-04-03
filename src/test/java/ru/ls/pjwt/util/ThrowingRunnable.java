package ru.ls.pjwt.util;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
