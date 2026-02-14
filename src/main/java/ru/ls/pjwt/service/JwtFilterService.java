package ru.ls.pjwt.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;
import ru.ls.pjwt.model.JwtToken;
import ru.ls.pjwt.utils.constants.Jwt;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtFilterService {
    private final List<HandlerMapping> handlerMapping;

    // todo check fingerprint (add in future)
    public UserDetails getUserDetails(String jwt) {
        JwtToken jwtToken = new JwtToken(jwt);
        jwtToken.checkType(Jwt.ACCESS);
        return jwtToken.getUserDetails();
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
