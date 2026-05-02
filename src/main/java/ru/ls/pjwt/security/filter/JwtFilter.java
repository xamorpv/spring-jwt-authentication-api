package ru.ls.pjwt.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.ls.pjwt.domain.token.service.jwt.JwtFilterService;

/**
 * A security filter that runs once per request and extracts JWT authentication from the {@code
 * Authorization} header.
 *
 * <p>It is placed before {@link UsernamePasswordAuthenticationFilter} in the Spring Security filter
 * chain and populates the security context if a valid access token is present.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final JwtFilterService jwtFilterService;

  private final HandlerExceptionResolver handlerExceptionResolver;

  @SuppressFBWarnings("SERVLET_HEADER")
  @SuppressWarnings(
      "PMD.AvoidCatchingGenericException") // Фильтр передает все ошибки в HandlerExceptionResolver
  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    final SecurityContext context = SecurityContextHolder.getContext();
    // если по какой-то причине пользователь уже аутентифицирован
    if (context.getAuthentication() != null) {
      log.warn(
          "user authenticated before filter. uri={}, ip={}",
          request.getRequestURI(),
          request.getRemoteAddr());
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String header = request.getHeader("Authorization");
      if (header == null || !header.startsWith("Bearer ") || "Bearer ".equals(header)) {
        log.debug("no Bearer token in request, skipping authentication");
        filterChain.doFilter(request, response);
        return;
      }

      final String jwt = header.substring(7);

      authenticateWithToken(jwt, context, request);

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      SecurityContextHolder.clearContext(); // стандарт безопасности
      log.warn("exception in filter: {}", e.getMessage());
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }

  private void authenticateWithToken(
      final String jwt, final SecurityContext context, final HttpServletRequest request) {
    final UserDetails userDetails = jwtFilterService.getUserDetails(jwt);
    final UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    context.setAuthentication(token);
    log.info("user {} authenticated with token", userDetails.getUsername());
  }
}
