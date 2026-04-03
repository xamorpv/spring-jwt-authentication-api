package ru.ls.pjwt.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtils {
    public String random() {
        return UUID.randomUUID().toString();
    }
}
