package ru.ls.pjwt.domain.token.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.common.web.exception.ServerError;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.token.exception.JwtTokenRequestException;

@Service
@RequiredArgsConstructor
public class JwtFilterService {
  private final JwtProperties jwtProperties;
  private final JwtParser jwtParser;
  private final JwtValidator jwtValidator;

  /**
   * Extracts user details from an access token.
   *
   * @param jwt the access token string
   * @return the {@link UserDetails} parsed from the token's claims
   * @throws JwtTokenRequestException if the token is invalid or expired
   * @throws ServerError if the token type claim is missing (unrecoverable configuration error)
   */
  public UserDetails getUserDetails(String jwt) {
    // todo check fingerprint (add in future)
    JwtClaims jwtClaims = jwtParser.parseAccessToken(jwt);
    jwtValidator.validateType(jwtProperties.getAccessToken(), jwtClaims);
    return jwtClaims.userDetails();
  }
}
