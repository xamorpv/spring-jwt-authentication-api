package ru.ls.pjwt.exception.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshTokenRaceConditionException extends RuntimeException {
    private final Long tokenId;
}
