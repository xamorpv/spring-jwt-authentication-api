package ru.ls.pjwt.domain.token.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshTokenRaceConditionException extends RuntimeException {
    private final Long tokenId;
}
