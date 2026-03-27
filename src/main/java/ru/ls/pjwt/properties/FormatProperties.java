package ru.ls.pjwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.format")
public record FormatProperties(String dateFormat) {
}
