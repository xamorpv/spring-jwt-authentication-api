package ru.ls.pjwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.service.jwt.JwtFilterService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher;
    private final JwtFilterService jwtFilterService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // пропускаем эндпоинты, которые не требуют аутентификации
        if (requestMatcher.matches(request)) {
            log.debug("skip: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtFilterService.isEndpointExists(request)) {
            log.debug("skip: {} (endpoint does not exists)", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContext context = SecurityContextHolder.getContext();
        // если по какой-то причине пользователь уже аутентифицирован
        if (context.getAuthentication() != null) {
            log.warn("user authenticated before filter. uri={}, ip={}", request.getRequestURI(), request.getRemoteAddr());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                log.debug("jwt token exception in filter: wrong header ({})", header);
                throw new JwtTokenRequestException("missing header Authorization: Bearer <token>");
            }

            String jwt = header.substring(7);

            UserDetails userDetails = jwtFilterService.getUserDetails(jwt);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            context.setAuthentication(token);

            log.info("user {} authenticated with token {}", userDetails, jwt);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("exception in filter: {}", e.getMessage(), e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}