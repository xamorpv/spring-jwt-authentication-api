package ru.ls.pjwt.common.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ls.pjwt.common.property.FormatProperties;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class TimeUtils {
    private final FormatProperties formatProperties;
    @Getter
    private DateTimeFormatter formatter;

    @PostConstruct
    private void initFormatter() {
        formatter = DateTimeFormatter.ofPattern(formatProperties.dateFormat()).withZone(ZoneOffset.UTC);
    }

    public String timestamp() {
        return Instant.now().atZone(ZoneOffset.UTC).format(formatter);
    }
}
