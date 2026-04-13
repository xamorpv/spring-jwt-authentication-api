package ru.ls.pjwt.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import ru.ls.pjwt.helper.token.AccessTokenHelper;
import ru.ls.pjwt.helper.token.RefreshTokenHelper;
import ru.ls.pjwt.helper.user.UserAuthenticationHelper;
import ru.ls.pjwt.helper.user.UserRegistrationHelper;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({AccessTokenHelper.class, RefreshTokenHelper.class, UserAuthenticationHelper.class, UserRegistrationHelper.class})
public class WebSecurityTest extends TestWithContainer{
    @Autowired
    protected RefreshTokenHelper refreshTokenHelper;

    @Autowired
    protected AccessTokenHelper accessTokenHelper;

    @Autowired
    protected UserAuthenticationHelper userAuthenticationHelper;

    @Autowired
    protected UserRegistrationHelper userRegistrationHelper;
}
