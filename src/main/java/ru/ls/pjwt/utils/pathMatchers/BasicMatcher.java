package ru.ls.pjwt.utils.pathMatchers;

import org.springframework.http.HttpMethod;

import java.util.List;

public record BasicMatcher(HttpMethod httpMethod, List<String> paths) {
}
