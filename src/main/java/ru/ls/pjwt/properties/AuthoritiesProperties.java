package ru.ls.pjwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pjwt.authorities")
public record AuthoritiesProperties(String user, String mod, String admin) {
}
