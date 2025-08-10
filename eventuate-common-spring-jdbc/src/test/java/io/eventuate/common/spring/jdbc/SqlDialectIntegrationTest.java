package io.eventuate.common.spring.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.SchemaAndTable;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.PostgresDialect;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import io.eventuate.common.spring.id.IdGeneratorConfiguration;
import io.eventuate.common.spring.jdbc.sqldialect.SqlDialectConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_APPLICATION_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_APPLICATION_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;
import static java.lang.System.nanoTime;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SqlDialectIntegrationTest.Config.class)
public class SqlDialectIntegrationTest {
  @Configuration
  @EnableAutoConfiguration
  @Import({SqlDialectConfiguration.class,
          EventuateCommonJdbcOperationsConfiguration.class,
          IdGeneratorConfiguration.class})
  public static class Config {}


  private static final int DEFAULT_DB_RECORDS = 10;

  private static final EventuateSchema DEFAULT_EVENTUATE_SCHEMA = new EventuateSchema(EventuateSchema.DEFAULT_SCHEMA);

  @Value("${spring.datasource.driver-class-name}")
  private String driver;

  @Value("${spring.datasource.url}")
  private String dataSourceUrl;

  @Value("${db.id.used:#{false}}")
  private boolean useDbId;

  @Value("${spring.profiles.active:#{null}}")
  private String profile;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private SqlDialectSelector sqlDialectSelector;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Autowired
  private IdGenerator idGenerator;

  @Test
  public void testPostgresColumnTypeAccess() {
    if (profile == null || !profile.contains("postgres")) {
      return;
    }

    String schema = "column_type_test_schema_" + nanoTime();

    jdbcTemplate.update("create schema %s".formatted(schema));

    String sampleTable = "column_type_test_table_" + nanoTime();

    jdbcTemplate.update("create table %s.%s(id text primary key)".formatted(schema, sampleTable));

    PostgresDialect postgresDialect = (PostgresDialect) getDialect();

    String columnType =
            postgresDialect
                    .getColumnType(
                            new EventuateSchema(schema),
                            sampleTable,
                            "id",
                            (sql, args) -> jdbcTemplate.queryForList(sql, args.toArray())
                    );

    assertEquals("text", columnType);
  }

  @Test
  public void testAddLimitToSimpleSelect() {
    final int LIMIT = 3;

    prepareRandomData();

    String sqlWithoutLimit = "select * from %s".formatted(DEFAULT_EVENTUATE_SCHEMA.qualifyTable("events"));
    String sqlWithLimit = getDialect().addLimitToSql(sqlWithoutLimit, String.valueOf(LIMIT));

    List<Map<String, Object>> resultWithLimit = jdbcTemplate.queryForList(sqlWithLimit);
    List<Map<String, Object>> resultWithoutLimit = jdbcTemplate.queryForList(sqlWithoutLimit);

    assertTrue(resultWithoutLimit.size() >= DEFAULT_DB_RECORDS);
    assertEquals(LIMIT, resultWithLimit.size());
  }

  @Test
  public void testAddLimitToSelectWithCondition() {
    final int LIMIT = 2;
    final int SELECTABLE_RECORDS = 3;

    prepareRandomData();

    String eventType = generateId();

    for (int i = 0; i < SELECTABLE_RECORDS; i++)
    {
      eventuateCommonJdbcOperations.insertIntoEventsTable(idGenerator,
              generateId(),
              generateId(),
              eventType,
              generateId(),
              Optional.empty(),
              Optional.empty(),
              DEFAULT_EVENTUATE_SCHEMA);
    }

    String sqlWithoutLimit = "select * from %s where event_type = ?".formatted(DEFAULT_EVENTUATE_SCHEMA.qualifyTable("events"));
    String sqlWithLimit = getDialect().addLimitToSql(sqlWithoutLimit, String.valueOf(LIMIT));

    List<Map<String, Object>> resultWithLimit = jdbcTemplate.queryForList(sqlWithLimit, eventType);
    List<Map<String, Object>> resultWithoutLimit = jdbcTemplate.queryForList(sqlWithoutLimit, eventType);

    assertEquals(SELECTABLE_RECORDS, resultWithoutLimit.size());
    assertEquals(LIMIT, resultWithLimit.size());

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

    assertTrue(dbTime > javaTime1);
    assertTrue(dbTime < javaTime2);
  }

  @Test
  public void testEventsPrimaryKeyColumn() throws SQLException {
    assertPrimaryKeyColumnEquals("events", singletonList(useDbId
            ? EVENT_AUTO_GENERATED_ID_COLUMN
            : EVENT_APPLICATION_GENERATED_ID_COLUMN));
  }

  @Test
  public void testMessagePrimaryKeyColumn() throws SQLException {
    assertPrimaryKeyColumnEquals("message", singletonList(useDbId
            ? MESSAGE_AUTO_GENERATED_ID_COLUMN
            : MESSAGE_APPLICATION_GENERATED_ID_COLUMN));
  }

  @Test
  public void testReceivedMessagePrimaryKeyColumn() throws SQLException {
    assertPrimaryKeyColumnEquals("received_messages", asList("consumer_id", "message_id"));
  }

  private void assertPrimaryKeyColumnEquals(String table, List<String> expectedKeyColumns) throws SQLException {
    List<String> pkColumns = getDialect()
            .getPrimaryKeyColumns(dataSource, dataSourceUrl, new SchemaAndTable(EventuateSchema.DEFAULT_SCHEMA, table));

    assertEquals(expectedKeyColumns, pkColumns);
  }

  private void assertAllRowsHaveTheSameEventType(List<Map<String, Object>> rows, String eventType) {
    assertTrue(rows.stream().allMatch(row -> row.get("event_type").equals(eventType)));
  }

  private void prepareRandomData() {
    for (int i = 0; i < DEFAULT_DB_RECORDS; i++) {
      eventuateCommonJdbcOperations.insertIntoEventsTable(idGenerator,
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
