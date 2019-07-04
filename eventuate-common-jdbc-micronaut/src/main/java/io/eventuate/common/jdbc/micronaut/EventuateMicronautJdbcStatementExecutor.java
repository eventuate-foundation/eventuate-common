package io.eventuate.common.jdbc.micronaut;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventuateMicronautJdbcStatementExecutor implements EventuateJdbcStatementExecutor {

  private EventuateMicronautTransactionManagement eventuateMicronautTransactionManagement;

  public EventuateMicronautJdbcStatementExecutor(EventuateMicronautTransactionManagement eventuateMicronautTransactionManagement) {
    this.eventuateMicronautTransactionManagement = eventuateMicronautTransactionManagement;
  }

  @Override
  public void update(String sql, Object... params) throws SQLException {

    eventuateMicronautTransactionManagement.doWithConnection(connection -> {
      try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        for (int i = 0; i < params.length; i++) {
          preparedStatement.setObject(i + 1, params[i]);
        }

        preparedStatement.executeUpdate();
      }
    });
  }
}
