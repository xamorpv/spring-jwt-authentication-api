package ru.ls.pjwt.service.auth.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ls.pjwt.dto.JwtClaims;
import ru.ls.pjwt.entity.User;
import ru.ls.pjwt.mapper.UserMapper;
import ru.ls.pjwt.service.jwt.JwtFactory;
import ru.ls.pjwt.service.jwt.JwtSecurity;
import ru.ls.pjwt.utils.constants.Jwt;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final JwtFactory jwtFactory;

    private final JwtSecurity jwtSecurity;
    private final UserMapper userMapper;

    public String createAccessToken(JwtClaims refreshTokenClaims, User user) {
        jwtSecurity.checkType(Jwt.REFRESH, refreshTokenClaims);
        return jwtFactory.createAccessToken(userMapper.userEntityToUserDetails(user));
    }
}
