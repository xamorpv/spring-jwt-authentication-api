package ru.ls.pjwt.base;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class TestWithContainer extends IntegrationTest {
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:18-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRESQL_CONTAINER.start(); // запускается ровно один раз
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.flyway.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.flyway.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void setUpDatabase() {
        flyway.clean();
        flyway.migrate();
    }
}
