package ru.ls.pjwt.common.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.format")
public record FormatProperties(String dateFormat) {}
