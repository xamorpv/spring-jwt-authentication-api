package ru.ls.pjwt.domain.token.service.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.web.exception.ServerError;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;

/**
 * Validates the type claim of a JWT token (access vs refresh).
 *
 * <p>This is a focused validation that ensures a token is used for the correct purpose (e.g., a
 * refresh token is not used as an access token).
 */
@Service
@Slf4j
public class JwtValidator {
  /**
   * Validates that the given JWT claims contain the expected token type.
   *
   * @param type the expected token type (e.g. "access" or "refresh")
   * @param claims the parsed JWT claims to verify
   * @throws ServerError if the token does not contain the 'type' claim
   * @throws JwtTokenRequestException if the token type does not match the expected value
   */
  public void validateType(final String type, final JwtClaims claims) {
    log.debug("check token type for user={}, expected type={}", claims.username(), type);
    final String tokenType = claims.claims().get("type", String.class);
    if (tokenType == null) {
      // токен не подделать, значит это какая-то ошибка разработчиков, т.е server error
      throw new ServerError("The token does not contain the 'type' claim");
    }
    if (!tokenType.equals(type)) {
      log.debug("wrong token type {}, expected: {}", tokenType, type);
      throw new JwtTokenRequestException("for this operation expected type was " + type);
    }
  }
}
