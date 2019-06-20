package io.eventuate.common.jdbc.micronaut;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventuateMicronautJdbcStatementExecutor implements EventuateJdbcStatementExecutor {

  private DataSource dataSource;

  public EventuateMicronautJdbcStatementExecutor(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void update(String sql, Object... params) throws SQLException {
    try (Connection connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      for (int i = 0; i < params.length; i++) {
        preparedStatement.setObject(i + 1, params[i]);
      }

      preparedStatement.executeUpdate();
    }
  }
}
