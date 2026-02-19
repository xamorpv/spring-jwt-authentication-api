package ru.ls.pjwt.dto;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public record JwtClaims(Claims claims, UserDetails userDetails, String uuid, String username, String token) {
}
