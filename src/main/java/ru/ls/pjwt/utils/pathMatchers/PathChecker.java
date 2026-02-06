package ru.ls.pjwt.utils.pathMatchers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathChecker {
    public boolean isCriticalEndpoint(HttpServletRequest request) {
        //todo реализовать (через аннотации - повесить над эндпоинтом - значит, что он критически важный) (когда будет готов fingerprint)
        return request.getRequestURI().equals("/api/v1/test/critical");
    }
}
