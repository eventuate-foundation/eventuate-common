package io.eventuate.common.testcontainers;

import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.spring.jdbc.EventuateCommonJdbcOperationsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = EventuatePostgresContainerTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuatePostgresContainerTest {

    public static EventuateDatabaseContainer<?> database = EventuatePostgresContainer.makeFromDockerfile();

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        PropertyProvidingContainer.startAndProvideProperties(registry, database);
    }


    @Configuration
    @EnableAutoConfiguration
    @Import(EventuateCommonJdbcOperationsConfiguration.class)
    public static class Config {
    }


    @Autowired
    private EventuateSchema eventuateSchema;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldSpecifyEventuateSchema() {
        jdbcTemplate.queryForList(String.format("select * from %s", eventuateSchema.qualifyTable("message")));
    }

    @Test
    public void shouldSpecifyMonitoringSchema() {
        jdbcTemplate.queryForList(String.format("select * from %s.cdc_monitoring", database.getMonitoringSchema()));
    }

}