package ru.ls.pjwt.base;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "dev"})
public class IntegrationTest {

  protected IntegrationTest() {
    // Constructor to prevent direct instantiation; this class is designed to be subclassed.
  }
}
