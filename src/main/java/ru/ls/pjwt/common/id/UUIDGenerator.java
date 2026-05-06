package ru.ls.pjwt.common.id;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator {
  /**
   * Returns a new random UUID string.
   *
   * @return a random UUID as a string, e.g. {@code "123e4567-e89b-12d3-a456-426614174000"}
   */
  public String random() {
    return UUID.randomUUID().toString();
  }
}
