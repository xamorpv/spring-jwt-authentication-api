package ru.ls.pjwt.common.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtils {
    public String random() {
        return UUID.randomUUID().toString();
    }
}
