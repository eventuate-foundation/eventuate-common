package io.eventuate.common.testcontainers;

import io.eventuate.common.spring.jdbc.EventuateCommonJdbcOperationsConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@SpringBootTest
public class EventuateVanillaMsSqlContainerTest {

    protected static Logger logger = LoggerFactory.getLogger(EventuateVanillaMsSqlContainerTest.class);

    public static EventuateDatabaseContainer<?> database =
        EventuateVanillaMsSqlContainer.make()
            .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SVC db:"))
        ;

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
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldAccessEventuateSchema() {
        jdbcTemplate.execute("create table dbo.foo(id VARCHAR(767) PRIMARY KEY)");
        jdbcTemplate.execute("select * from dbo.foo");
        jdbcTemplate.execute("select * from foo");
        jdbcTemplate.execute("create table dbo.bar(id VARCHAR(767) PRIMARY KEY)");
        jdbcTemplate.execute("select * from dbo.bar");
        jdbcTemplate.execute("select * from bar");
        jdbcTemplate.execute("create table baz(id VARCHAR(767) PRIMARY KEY)");
        jdbcTemplate.execute("select * from dbo.baz");
        jdbcTemplate.execute("select * from baz");
        // jdbcTemplate.execute("select * from foo");
    }


}