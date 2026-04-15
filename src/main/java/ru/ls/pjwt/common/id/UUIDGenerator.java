package ru.ls.pjwt.common.id;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator {
    public String random() {
        return UUID.randomUUID().toString();
    }
}
