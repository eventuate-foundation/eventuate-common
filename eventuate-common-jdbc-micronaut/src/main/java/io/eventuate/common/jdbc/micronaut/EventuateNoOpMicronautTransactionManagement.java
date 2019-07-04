package io.eventuate.common.jdbc.micronaut;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class EventuateNoOpMicronautTransactionManagement implements EventuateMicronautTransactionManagement {

  private DataSource dataSource;

  public EventuateNoOpMicronautTransactionManagement(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void doWithConnection(TransactionCallback transactionalSection) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(true);
      transactionalSection.execute(connection);
    }
  }
}
