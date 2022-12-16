package io.eventuate.common.flyway;

import io.eventuate.common.spring.jdbc.EventuateCommonJdbcOperationsConfiguration;
import io.eventuate.common.testcontainers.DatabaseContainerFactory;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = FlywayTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties="spring.flyway.locations=classpath:flyway/{vendor}")
public class FlywayTest {

    public static EventuateDatabaseContainer<?> database = DatabaseContainerFactory.makeVanillaDatabaseContainerFromDockerFile();

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        PropertyProvidingContainer.startAndProvideProperties(registry, database);
    }


    @Configuration
    @EnableAutoConfiguration
    @Import(EventuateCommonJdbcOperationsConfiguration.class)
    public static class Config {
    }

    @Test
    public void shouldInitializationDatabase() {

    }
}
