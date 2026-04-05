package ru.ls.pjwt.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.application")
public record ApplicationProperties(String name) {
}
