package ru.ls.pjwt.domain.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.common.web.exception.ServerError;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;
import ru.ls.pjwt.domain.token.service.jwt.JwtFactory;
import ru.ls.pjwt.domain.token.service.jwt.JwtValidator;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.mapper.UserToDetailsMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {
  private final JwtProperties jwtProperties;
  private final JwtFactory jwtFactory;
  private final JwtValidator jwtValidator;
  private final UserToDetailsMapper userToDetailsMapper;

  /**
   * Creates a new access token from refresh token claims and user details.
   *
   * @param refreshTokenClaims the parsed claims from a valid refresh token
   * @param user the authenticated user entity
   * @return a signed access token string
   * @throws JwtTokenRequestException if the token type is invalid
   * @throws ServerError if the token type claim is missing (unrecoverable configuration error)
   */
  public String createAccessToken(JwtClaims refreshTokenClaims, User user) {
    jwtValidator.validateType(jwtProperties.getRefreshToken(), refreshTokenClaims);
    return jwtFactory.createAccessToken(userToDetailsMapper.userEntityToUserDetails(user));
  }
}
