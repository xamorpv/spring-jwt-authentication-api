package ru.ls.pjwt.domain.token.service.refresh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.common.property.JwtProperties;
import ru.ls.pjwt.domain.auth.property.DevUsernamesProperties;
import ru.ls.pjwt.fixture.JwtTokenFixture;
import ru.ls.pjwt.steps.token.RefreshTokenSteps;

@Import({RefreshTokenSteps.class})
public class RefreshTokenNotFoundIntegrationTest extends WebIntegrationTest {
    @Autowired
    private DevUsernamesProperties devUsernamesProperties;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenFixture jwtTokenFixture;

    @Autowired
    private RefreshTokenSteps refreshTokenSteps;

    @Test
    void shouldReturn401WhenGivenNonExistentRefreshToken() throws Exception {
        String refreshTokenWithoutUUID = jwtTokenFixture.getBaseJwtBuilder()
                .subject(devUsernamesProperties.user())
                .claim("type", jwtProperties.getRefreshToken())
                .claim("uuid", "123456770881adfkajdfskjfa")
                .compact();
        refreshTokenSteps.refreshExpectingError(refreshTokenWithoutUUID, HttpStatus.UNAUTHORIZED);
    }
}
