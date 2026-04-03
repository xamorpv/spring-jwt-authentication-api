package ru.ls.pjwt.domain.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.domain.token.dto.JwtClaims;
import ru.ls.pjwt.domain.user.entity.User;
import ru.ls.pjwt.domain.user.mapper.UserMapper;
import ru.ls.pjwt.domain.token.service.jwt.JwtFactory;
import ru.ls.pjwt.domain.token.service.jwt.JwtValidator;
import ru.ls.pjwt.common.property.JwtProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final JwtProperties jwtProperties;
    private final JwtFactory jwtFactory;
    private final JwtValidator jwtValidator;
    private final UserMapper userMapper;

    public String createAccessToken(JwtClaims refreshTokenClaims, User user) {
        jwtValidator.validateType(jwtProperties.getRefreshToken(), refreshTokenClaims);
        return jwtFactory.createAccessToken(userMapper.userEntityToUserDetails(user));
    }
}
