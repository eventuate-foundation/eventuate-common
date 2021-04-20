package io.eventuate.common.micronaut.data.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateCommonJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
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
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  @Inject
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Inject
  private EventuateCommonJdbcStatementExecutor eventuateCommonJdbcStatementExecutor;

  @Inject
  private DataSource dataSource;

  @Inject
  private IdGenerator idGenerator;


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
  protected EventuateTransactionTemplate getEventuateTransactionTemplate() {
    return eventuateTransactionTemplate;
  }

  @Override
  protected DataSource getDataSource() {
    return dataSource;
  }

  @Override
  protected IdGenerator getIdGenerator() {
    return idGenerator;
  }

  @Override
  protected String insertIntoMessageTable(String payload,
                                          String destination,
                                          Map<String, String> headers) {

    return eventuateTransactionTemplate.executeInTransaction(() ->
            eventuateCommonJdbcOperations.insertIntoMessageTable(idGenerator,
                    payload,
                    destination,
                    headers,
                    eventuateSchema)
    );
  }

  @Override
  protected String insertIntoEventsTable(String entityId,
                                         String eventData,
                                         String eventType,
                                         String entityType,
                                         Optional<String> triggeringEvent,
                                         Optional<String> metadata) {

    return eventuateTransactionTemplate.executeInTransaction(() ->
            eventuateCommonJdbcOperations.insertIntoEventsTable(idGenerator,
                    entityId, eventData, eventType, entityType, triggeringEvent, metadata, eventuateSchema));
  }

  @Override
  protected void insertIntoEntitiesTable(String entityId, String entityType, EventuateSchema eventuateSchema) {
    String table = eventuateSchema.qualifyTable("entities");
    String sql = String.format("insert into %s values (?, ?, ?);", table);

    eventuateTransactionTemplate.executeInTransaction(() ->
            eventuateCommonJdbcStatementExecutor.update(sql, entityId, entityType, System.nanoTime()));
  }

  @Override
  protected EventuateSqlDialect getEventuateSqlDialect() {
    return eventuateCommonJdbcOperations.getEventuateSqlDialect();
  }
}
