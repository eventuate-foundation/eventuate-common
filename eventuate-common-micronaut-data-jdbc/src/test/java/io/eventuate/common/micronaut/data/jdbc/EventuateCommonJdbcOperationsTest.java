package io.eventuate.common.micronaut.data.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;


@MicronautTest(transactional = false)
public class EventuateCommonJdbcOperationsTest extends AbstractEventuateCommonJdbcOperationsTest {

  @Inject
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Inject
  private EventuateTransactionTemplate eventuateTransactionTemplate;

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
  protected EventuateCommonJdbcOperations getEventuateCommonJdbcOperations() {
    return eventuateCommonJdbcOperations;
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
}
