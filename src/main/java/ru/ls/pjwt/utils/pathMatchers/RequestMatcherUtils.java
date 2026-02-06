package ru.ls.pjwt.utils.pathMatchers;

import lombok.experimental.UtilityClass;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class RequestMatcherUtils {
    public OrRequestMatcher createOrRequestMatherFluent(FluentOrRequestMatcher fluentOrRequestMatcher) {
        return new OrRequestMatcher(fluentOrRequestMatcher.getBasicMatchers().stream()
                .map(matcher -> {
                    List<RequestMatcher> regexMatchers = new ArrayList<>();
                    for (String path : matcher.paths()) {
                        regexMatchers.add(new RegexRequestMatcher(path, matcher.httpMethod().toString()));
                    }
                    return regexMatchers;
                })
                .flatMap(Collection::stream)
                .toList());
    }
}
