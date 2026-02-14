package ru.ls.pjwt.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UUIDUtils {
    public String random() {
        return UUID.randomUUID().toString();
    }
}
