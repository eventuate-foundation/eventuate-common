package io.eventuate.common.spring.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.SchemaAndTable;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import io.eventuate.common.spring.id.IdGeneratorConfiguration;
import io.eventuate.common.spring.jdbc.sqldialect.SqlDialectConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_APPLICATION_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_APPLICATION_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

@SpringBootTest(classes = SqlDialectIntegrationTest.Config.class)
@RunWith(SpringRunner.class)
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
  public void testAddLimitToSimpleSelect() {
    final int LIMIT = 3;

    prepareRandomData();

    String sqlWithoutLimit = String.format("select * from %s", DEFAULT_EVENTUATE_SCHEMA.qualifyTable("events"));
    String sqlWithLimit = getDialect().addLimitToSql(sqlWithoutLimit, String.valueOf(LIMIT));

    List<Map<String, Object>> resultWithLimit = jdbcTemplate.queryForList(sqlWithLimit);
    List<Map<String, Object>> resultWithoutLimit = jdbcTemplate.queryForList(sqlWithoutLimit);

    Assert.assertTrue(resultWithoutLimit.size() >= DEFAULT_DB_RECORDS);
    Assert.assertEquals(LIMIT, resultWithLimit.size());
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

  @Test
  public void testEventsPrimaryKeyColumn() throws SQLException {
    assertPrimaryKeyColumnEquals("events", singleton(useDbId
            ? EVENT_AUTO_GENERATED_ID_COLUMN
            : EVENT_APPLICATION_GENERATED_ID_COLUMN));
  }

  @Test
  public void testMessagePrimaryKeyColumn() throws SQLException {
    assertPrimaryKeyColumnEquals("message", singleton(useDbId
            ? MESSAGE_AUTO_GENERATED_ID_COLUMN
            : MESSAGE_APPLICATION_GENERATED_ID_COLUMN));
  }

  @Test
  public void testReceivedMessagePrimaryKeyColumn() throws SQLException {
    assertPrimaryKeyColumnEquals("received_messages", new HashSet<>(asList("message_id", "consumer_id")));
  }

  private void assertPrimaryKeyColumnEquals(String table, Set<String> expectedKeyColumns) throws SQLException {
    List<String> pkColumns = getDialect()
            .getPrimaryKeyColumns(dataSource, dataSourceUrl, new SchemaAndTable(EventuateSchema.DEFAULT_SCHEMA, table));

    Assert.assertEquals(expectedKeyColumns, new HashSet<>(pkColumns));
  }

  private void assertAllRowsHaveTheSameEventType(List<Map<String, Object>> rows, String eventType) {
    Assert.assertTrue(rows.stream().allMatch(row -> row.get("event_type").equals(eventType)));
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
