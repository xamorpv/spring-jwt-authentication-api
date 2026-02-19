package ru.ls.pjwt.utils;

import lombok.experimental.UtilityClass;
import ru.ls.pjwt.utils.constants.Format;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtils {
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Format.dateFormat).withZone(ZoneOffset.UTC);

    public String timestamp() {
        return Instant.now().atZone(ZoneOffset.UTC).format(formatter);
    }
}
