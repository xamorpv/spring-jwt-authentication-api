package ru.ls.pjwt.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.application")
public record ApplicationProperties(String name) {
}
