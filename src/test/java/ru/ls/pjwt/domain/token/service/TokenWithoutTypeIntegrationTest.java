package ru.ls.pjwt.domain.token.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.common.property.AuthoritiesProperties;
import ru.ls.pjwt.fixture.JwtTokenFixture;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;

import java.util.List;

@Import(RefreshTokenSteps.class)
public class TokenWithoutTypeIntegrationTest extends WebIntegrationTest {
    @Autowired
    private MockMvcClient mockMvcClient;

    @Autowired
    private AuthoritiesProperties authoritiesProperties;

    @Autowired
    private JwtTokenFixture jwtTokenFixture;

    @Autowired
    private RefreshTokenSteps refreshTokenSteps;

    @Test
    void shouldReturn500WhenGivenAccessTokenWithoutType() throws Exception {
        String jwtWithoutType = jwtTokenFixture.getBaseJwtTokenBuilder()
                .claim("authorities", List.of(authoritiesProperties.user()))
                .compact();

        mockMvcClient.getProtectedDataExpecting500("Bearer " + jwtWithoutType);
    }

    @Test
    void shouldReturn500WhenGivenRefreshTokenWithoutType() throws Exception {
        String jwtWithoutType = jwtTokenFixture.getBaseJwtTokenBuilder().compact();
        refreshTokenSteps.refreshExpectingError(jwtWithoutType, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
