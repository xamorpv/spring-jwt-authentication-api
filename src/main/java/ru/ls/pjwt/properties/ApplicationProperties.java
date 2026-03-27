package ru.ls.pjwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.application")
public record ApplicationProperties(String name) {
}
