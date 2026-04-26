package ru.ls.pjwt.domain.token.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.common.property.ExceptionsProperties;
import ru.ls.pjwt.domain.token.dto.CreatedRefreshToken;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.entity.RefreshToken;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.repository.RefreshTokenRepository;
import ru.ls.pjwt.domain.user.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final ExceptionsProperties exceptionsProperties;
  private final RefreshTokenRepository refreshTokenRepository;
  private final RefreshTokenValidator refreshTokenValidator;
  private final RefreshTokenManager refreshTokenManager;

  /**
   * Marks the refresh token identified by the given claims as used.
   *
   * @param claims the parsed JWT claims containing the token UUID
   * @throws BadCredentialsException if the claims do not contain a UUID
   * @throws JwtTokenRequestException if the token is not found
   */
  @Transactional
  public void markTokenAsUsed(JwtClaims claims) {
    RefreshToken refreshToken = getToken(claims);
    log.debug("marking token as used: {}", refreshToken.getUuid());
    refreshTokenManager.use(refreshToken);
  }

  /**
   * Finds and validates a refresh token by UUID from the provided JWT claims.
   *
   * @param jwtClaims the parsed JWT claims containing the token UUID
   * @return the corresponding {@link RefreshToken} entity
   * @throws BadCredentialsException if the claims do not contain a UUID
   * @throws JwtTokenRequestException if the token is not found or has already been used
   */
  @Transactional
  public RefreshToken getToken(JwtClaims jwtClaims) {
    String uuid = jwtClaims.uuid();
    if (uuid == null) {
      log.warn("jwt token without uuid, may be deprecated");
      throw new BadCredentialsException(exceptionsProperties.badCredentials());
    }
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByUuid(uuid)
            .orElseThrow(() -> new JwtTokenRequestException("token not found"));

    refreshTokenValidator.checkUsed(refreshToken);
    return refreshToken;
  }

  /**
   * Saves a new refresh token entity for the specified user.
   *
   * @param createdRefreshToken the token data to persist (contains UUID and token string)
   * @param user the user who owns this token
   * @return the persisted {@link RefreshToken} entity
   */
  @Transactional
  public RefreshToken save(CreatedRefreshToken createdRefreshToken, User user) {
    return refreshTokenRepository.save(new RefreshToken(createdRefreshToken.uuid(), user));
  }
}
