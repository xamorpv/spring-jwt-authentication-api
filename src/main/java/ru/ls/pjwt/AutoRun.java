package ru.ls.pjwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoRun implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("application started");
    }
}