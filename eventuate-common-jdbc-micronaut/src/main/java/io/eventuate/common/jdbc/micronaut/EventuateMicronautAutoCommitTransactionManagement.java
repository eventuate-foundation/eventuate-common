package io.eventuate.common.jdbc.micronaut;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class EventuateMicronautAutoCommitTransactionManagement implements EventuateMicronautTransactionManagement {

  private DataSource dataSource;

  public EventuateMicronautAutoCommitTransactionManagement(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void doWithTransaction(Consumer<Connection> callback) {
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(true);
      callback.accept(connection);
    } catch (SQLException sqlException) {
      throw new EventuateMicronautSqlException(sqlException);
    }
  }
}
