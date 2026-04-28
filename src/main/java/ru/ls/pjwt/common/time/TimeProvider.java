package ru.ls.pjwt.common.time;

import jakarta.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.property.FormatProperties;

@RequiredArgsConstructor
@Component
public class TimeProvider {
  private final Clock clock;
  private final FormatProperties formatProperties;

  @SuppressWarnings("NullAway.Init")
  @Getter
  private DateTimeFormatter formatter;

  @PostConstruct
  private void initFormatter() {
    formatter = DateTimeFormatter.ofPattern(formatProperties.dateFormat()).withZone(ZoneOffset.UTC);
  }

  /**
   * Formats the current UTC time according to the configured pattern.
   *
   * @return formatted timestamp string (e.g. {@code 2026-04-23__13:36:59})
   */
  public String timestamp() {
    return Instant.now(clock).atZone(ZoneOffset.UTC).format(formatter);
  }
}
