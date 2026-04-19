package ru.ls.pjwt.domain.token.service.jwt;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ls.pjwt.base.WebIntegrationTest;
import ru.ls.pjwt.client.MockMvcClient;
import ru.ls.pjwt.fixture.StandardUserFixture;

public class AccessTokenHeaderIntegrationTest extends WebIntegrationTest {
    @Autowired
    private MockMvcClient mockMvcClient;

    @ParameterizedTest
    @ValueSource(strings = {"Bearer ", "Basic " + StandardUserFixture.DEFAULT_USERNAME + ":" + StandardUserFixture.DEFAULT_PASSWORD})
    void shouldReturn401WhenGivenWrongHeader(String headerValue) throws Exception {
        mockMvcClient.getProtectedDataExpecting401(headerValue);
    }
}
