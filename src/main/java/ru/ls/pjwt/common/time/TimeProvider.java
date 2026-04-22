package ru.ls.pjwt.common.time;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.property.FormatProperties;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class TimeProvider {
    private final Clock clock;
    private final FormatProperties formatProperties;
    @Getter
    private DateTimeFormatter formatter;

    @PostConstruct
    private void initFormatter() {
        formatter = DateTimeFormatter.ofPattern(formatProperties.dateFormat()).withZone(ZoneOffset.UTC);
    }

    public String timestamp() {
        return Instant.now(clock).atZone(ZoneOffset.UTC).format(formatter);
    }
}
