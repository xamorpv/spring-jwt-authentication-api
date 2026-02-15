package ru.ls.pjwt.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;
import ru.ls.pjwt.model.JwtClaims;
import ru.ls.pjwt.utils.constants.Jwt;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtFilterService {
    private final List<HandlerMapping> handlerMapping;
    private final JwtClaimsFactory claimsFactory;

    // todo check fingerprint (add in future)
    public UserDetails getUserDetails(String jwt) {
        JwtClaims jwtClaims = claimsFactory.createJwtClaims(jwt);
        jwtClaims.checkType(Jwt.ACCESS);
        return jwtClaims.getUserDetails();
    }

    public boolean isEndpointExists(HttpServletRequest request) {
        for (HandlerMapping handler : handlerMapping) {
            try {
                if (handler.getHandler(request) != null) {
                    return true;
                }
            } catch (Exception ignored) {

            }
        }

        return false;
    }
}
