package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateSchema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@SpringBootTest(classes = SqlDialectIntegrationTest.Config.class)
@RunWith(SpringRunner.class)
public class SqlDialectIntegrationTest {
  @Configuration
  @EnableAutoConfiguration
  @Import(SqlDialectConfiguration.class)
  public static class Config {
    @Bean
    public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(JdbcTemplate jdbcTemplate) {
      return new EventuateCommonJdbcOperations(jdbcTemplate);
    }
  }

  private static final int DEFAULT_DB_RECORDS = 10;

  private static final EventuateSchema DEFAULT_EVENTUATE_SCHEMA = new EventuateSchema(EventuateSchema.DEFAULT_SCHEMA);

  @Value("${spring.datasource.driver-class-name}")
  private String driver;

  @Autowired
  private SqlDialectSelector sqlDialectSelector;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Test
  public void testAddLimitToSimpleSelect() {
    final int LIMIT = 3;

    clearEventsTable();
    prepareRandomData();

    String sqlWithoutLimit = String.format("select * from %s", DEFAULT_EVENTUATE_SCHEMA.qualifyTable("events"));
    String sqlWithLimit = getDialect().addLimitToSql(sqlWithoutLimit, String.valueOf(LIMIT));

    List<Map<String, Object>> resultWithLimit = jdbcTemplate.queryForList(sqlWithLimit);
    List<Map<String, Object>> resultWithoutLimit = jdbcTemplate.queryForList(sqlWithoutLimit);

    Assert.assertEquals(DEFAULT_DB_RECORDS, resultWithoutLimit.size());
    Assert.assertEquals(LIMIT, resultWithLimit.size());
  }

  @Test
  public void testAddLimitToSelectWithCondition() {
    final int LIMIT = 2;
    final int SELECTABLE_RECORDS = 3;

    clearEventsTable();
    prepareRandomData();

    String eventType = generateId();

    for (int i = 0; i < SELECTABLE_RECORDS; i++)
    {
      eventuateCommonJdbcOperations.insertIntoEventsTable(generateId(),
              generateId(),
              generateId(),
              eventType,
              generateId(),
              Optional.empty(),
              Optional.empty(),
              DEFAULT_EVENTUATE_SCHEMA);
    }

    String sqlWithoutLimit = String.format("select * from %s where event_type = ?", DEFAULT_EVENTUATE_SCHEMA.qualifyTable("events"));
    String sqlWithLimit = getDialect().addLimitToSql(sqlWithoutLimit, String.valueOf(LIMIT));

    List<Map<String, Object>> resultWithLimit = jdbcTemplate.queryForList(sqlWithLimit, eventType);
    List<Map<String, Object>> resultWithoutLimit = jdbcTemplate.queryForList(sqlWithoutLimit, eventType);

    Assert.assertEquals(SELECTABLE_RECORDS, resultWithoutLimit.size());
    Assert.assertEquals(LIMIT, resultWithLimit.size());

    assertAllRowsHaveTheSameEventType(resultWithoutLimit, eventType);
    assertAllRowsHaveTheSameEventType(resultWithLimit, eventType);
  }

  @Test
  public void testCurrentTime() throws InterruptedException {
    Long javaTime1 = System.currentTimeMillis();
    Thread.sleep(100);
    Long dbTime = jdbcTemplate.queryForObject("select " +  getDialect().getCurrentTimeInMillisecondsExpression(), Long.class);
    Thread.sleep(100);
    Long javaTime2 = System.currentTimeMillis();

    Assert.assertTrue(dbTime > javaTime1);
    Assert.assertTrue(dbTime < javaTime2);
  }

  private void assertAllRowsHaveTheSameEventType(List<Map<String, Object>> rows, String eventType) {
    Assert.assertTrue(rows.stream().allMatch(row -> row.get("event_type").equals(eventType)));
  }

  private void clearEventsTable() {
    jdbcTemplate.execute(String.format("truncate table %s", DEFAULT_EVENTUATE_SCHEMA.qualifyTable("events")));
  }

  private void prepareRandomData() {
    for (int i = 0; i < DEFAULT_DB_RECORDS; i++) {
      eventuateCommonJdbcOperations.insertIntoEventsTable(generateId(),
              generateId(),
              generateId(),
              generateId(),
              generateId(),
              Optional.empty(),
              Optional.empty(),
              DEFAULT_EVENTUATE_SCHEMA);
    }
  }

  private String generateId() {
    return UUID.randomUUID().toString();
  }

  private EventuateSqlDialect getDialect() {
    return sqlDialectSelector.getDialect(driver);
  }

}
