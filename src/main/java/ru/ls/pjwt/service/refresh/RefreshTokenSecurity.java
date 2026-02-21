package ru.ls.pjwt.service.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ls.pjwt.entity.RefreshToken;
import ru.ls.pjwt.exception.exceptions.JwtTokenRequestException;
import ru.ls.pjwt.repository.RefreshTokenRepository;
import ru.ls.pjwt.service.TransactionExecutor;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenSecurity {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TransactionExecutor transactionExecutor;
    private final RefreshTokenOperator refreshTokenOperator;

    /**
     * проверяет - был ли использован токен
     * если был использован, то нужно сделать проверку:
     * был ли уже до этого компроментирован:
     * если да, то можно спокойно бросать исключение
     * если нет, то нужно сделать все токены использованными + компроментированными (почему мы отключаем все токены? мы не знаем, кто первый использовал токен:
     * может быть, сейчас залогинился легитимный пользователь, а может - злоумышленник. в любом случае - нужно закрыть доступ к токенам, раз их кто-то использовал.
     * но чтобы злоумышленник не мог каждый раз отправлять один и тот же просроченный токен, который когда-то получил, и сбрасывать все токены пользователю, был введен параметр compromised
     */
    public void checkUsed(RefreshToken refreshToken) {
        if (refreshToken.isUsed()) {
            log.warn("token already used: {}", refreshToken);
            if (refreshToken.isCompromised()) {
                log.debug("token already compromised; throw exception and do nothing");
            } else {
                log.warn("token was not compromised before; using all tokens for this user");

                transactionExecutor.executeInIndependentTransaction(() ->
                {
                    // вместо выгрузки всех токенов память делаем операцию за один запрос
                    // todo fix race condition
                    // одновременно 2 запроса могут попасть сюда, и оба обновить токены. взлом не будет обнаружен
                    refreshTokenRepository.useAndCompromise(refreshToken.getUser().getUsername());
                    refreshTokenOperator.compromise(refreshToken);
                });
            }
            throw new JwtTokenRequestException("refresh token was compromised. you may be get hacked. please re-login");
        }
    }
}
