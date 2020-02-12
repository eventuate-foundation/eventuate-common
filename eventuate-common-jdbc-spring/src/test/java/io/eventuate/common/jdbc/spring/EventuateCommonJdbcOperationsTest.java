package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(classes = EventuateCommonJdbcOperationsTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateCommonJdbcOperationsTest extends AbstractEventuateCommonJdbcOperationsTest {

  @Configuration
  @EnableAutoConfiguration
  @Import(EventuateCommonJdbcOperationsConfiguration.class)
  public static class Config {
  }

  @Autowired
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Autowired
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  @Autowired
  private DataSource dataSource;

  @Test(expected = EventuateDuplicateKeyException.class)
  @Override
  public void testEventuateDuplicateKeyException() {
    super.testEventuateDuplicateKeyException();
  }

  @Test
  @Override
  public void testInsertIntoEventsTable() throws SQLException {
    super.testInsertIntoEventsTable();
  }

  @Test
  @Override
  public void testInsertIntoMessageTable() throws SQLException {
    super.testInsertIntoMessageTable();
  }

  @Override
  protected void insertIntoMessageTable(String messageId, String payload, String destination, String currentTimeInMillisecondsSql, Map<String, String> headers, EventuateSchema eventuateSchema) {
    eventuateTransactionTemplate.executeInTransaction(() -> {
      eventuateCommonJdbcOperations.insertIntoMessageTable(messageId,
              payload,
              destination,
              currentTimeInMillisecondsSql,
              headers,
              eventuateSchema);

      return null;
    });
  }

  @Override
  protected void insertIntoEventsTable(String eventId, String entityId, String eventData, String eventType, String entityType, Optional<String> triggeringEvent, Optional<String> metadata, EventuateSchema eventuateSchema) {
    eventuateTransactionTemplate.executeInTransaction(() -> {
      eventuateCommonJdbcOperations.insertIntoEventsTable(eventId,
              entityId, eventData, eventType, entityType, triggeringEvent, metadata, eventuateSchema);

      return null;
    });
  }

  @Override
  protected DataSource getDataSource() {
    return dataSource;
  }
}
