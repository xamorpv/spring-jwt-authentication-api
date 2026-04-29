package ru.ls.pjwt.startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartupLogger implements CommandLineRunner {
  @Override
  public void run(final String... args) {
    log.info("application started");
  }
}
