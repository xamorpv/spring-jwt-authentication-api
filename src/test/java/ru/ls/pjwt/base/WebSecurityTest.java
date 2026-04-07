package ru.ls.pjwt.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import ru.ls.pjwt.helper.RefreshTokenHelper;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class WebSecurityTest extends TestWithContainer{
    @Autowired
    protected RefreshTokenHelper refreshTokenHelper;
}
