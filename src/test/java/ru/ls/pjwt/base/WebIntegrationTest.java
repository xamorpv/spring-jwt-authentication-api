package ru.ls.pjwt.base;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import ru.ls.pjwt.client.MockMvcClient;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(MockMvcClient.class)
public class WebIntegrationTest extends DatabaseIntegrationTest {}
