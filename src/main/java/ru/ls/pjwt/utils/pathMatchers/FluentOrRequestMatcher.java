package ru.ls.pjwt.utils.pathMatchers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class FluentOrRequestMatcher {
    @Getter
    private final List<BasicMatcher> basicMatchers = new ArrayList<>();

    public FluentOrRequestMatcher or(HttpMethod httpMethod, String... pattern) {
        add(httpMethod, pattern);
        return this;
    }

    private void add(HttpMethod httpMethod, String... pattern) {
        basicMatchers.add(new BasicMatcher(httpMethod, List.of(pattern)));
    }
}
