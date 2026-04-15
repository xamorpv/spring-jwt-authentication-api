package ru.ls.pjwt.base;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "dev"})
public abstract class IntegrationTest {
}
