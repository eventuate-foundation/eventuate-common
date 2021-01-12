package io.eventuate.common.quarkus.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;

@QuarkusTest
public class EventuateCommonJdbcOperationsTest extends AbstractEventuateCommonJdbcOperationsTest {
  @Inject
  EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Inject
  EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;

  @Inject
  EventuateTransactionTemplate eventuateTransactionTemplate;

  @Inject
  Instance<DataSource> dataSource;

  @Inject
  IdGenerator idGenerator;

  @Test
  @Override
  public void testEventuateDuplicateKeyException() {
    Assertions.assertThrows(EventuateDuplicateKeyException.class, super::testEventuateDuplicateKeyException);
  }

  @Test
  @Override
  public void testGeneratedIdOfEventsTableRow() {
    super.testGeneratedIdOfEventsTableRow();
  }

  @Test
  @Override
  public void testGeneratedIdOfMessageTableRow() {
    super.testGeneratedIdOfMessageTableRow();
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
  protected EventuateCommonJdbcOperations getEventuateCommonJdbcOperations() {
    return eventuateCommonJdbcOperations;
  }

  @Override
  protected EventuateTransactionTemplate getEventuateTransactionTemplate() {
    return eventuateTransactionTemplate;
  }

  @Override
  protected IdGenerator getIdGenerator() {
    return idGenerator;
  }

  @Override
  protected DataSource getDataSource() {
    return dataSource.get();
  }

  @Override
  protected EventuateJdbcStatementExecutor getEventuateJdbcStatementExecutor() {
    return eventuateJdbcStatementExecutor;
  }
}
