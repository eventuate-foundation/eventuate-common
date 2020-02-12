package io.eventuate.common.jdbc.micronaut.spring;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;


@MicronautTest(transactional = false)
public class EventuateCommonJdbcOperationsTest extends AbstractEventuateCommonJdbcOperationsTest {

  @Inject
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Inject
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  @Inject
  private DataSource dataSource;

  @Test
  @Override
  public void testEventuateDuplicateKeyException() {
    Assertions.assertThrows(EventuateDuplicateKeyException.class, super::testEventuateDuplicateKeyException);
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
