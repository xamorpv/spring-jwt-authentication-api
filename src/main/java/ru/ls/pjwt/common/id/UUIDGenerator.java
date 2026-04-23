package ru.ls.pjwt.common.id;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator {
  public String random() {
    return UUID.randomUUID().toString();
  }
}
