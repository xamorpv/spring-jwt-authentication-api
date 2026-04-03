package ru.ls.pjwt.service.token.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.properties.ExceptionsProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenSecurity {
    private final ExceptionsProperties exceptionsProperties;
    private final RefreshTokenOperator refreshTokenOperator;

    /*
     проверяет - был ли использован токен
     если был использован, то нужно сделать проверку:
     был ли уже до этого компроментирован:
     если да, то можно спокойно бросать исключение
     если нет, то нужно сделать все токены использованными + компроментированными (почему мы отключаем все токены? мы не знаем, кто первый использовал токен:
     может быть, сейчас залогинился легитимный пользователь, а может - злоумышленник. в любом случае - нужно закрыть доступ к токенам, раз их кто-то использовал.
     но чтобы злоумышленник не мог каждый раз отправлять один и тот же просроченный токен, который когда-то получил, и сбрасывать все токены пользователю, был введен параметр compromised
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = JwtTokenRequestException.class)
    public void checkUsed(RefreshToken refreshToken) {
        if (refreshTokenOperator.compromiseIfUsed(refreshToken)) {
            throw new JwtTokenRequestException(exceptionsProperties.refreshTokenCompromised());
        }
    }
}
