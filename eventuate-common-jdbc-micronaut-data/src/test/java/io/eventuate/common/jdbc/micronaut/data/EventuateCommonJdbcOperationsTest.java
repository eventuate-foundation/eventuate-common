package io.eventuate.common.jdbc.micronaut.data;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.tests.AbstractEventuateCommonJdbcOperationsTest;
import io.micronaut.test.annotation.MicronautTest;
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

  @Test
  public void testInsertIntoEventsTable() throws SQLException {
    super.testInsertIntoEventsTable();
  }

  @Test
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
}
