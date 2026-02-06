package ru.ls.pjwt.utils;

import lombok.experimental.UtilityClass;
import ru.ls.pjwt.utils.constants.Format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtils {
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Format.dateFormat);

    public String timestamp() {
        return LocalDateTime.now().format(formatter);
    }
}
