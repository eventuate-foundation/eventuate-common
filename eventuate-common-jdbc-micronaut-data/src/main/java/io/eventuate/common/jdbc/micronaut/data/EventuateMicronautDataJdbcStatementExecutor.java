package io.eventuate.common.jdbc.micronaut.data;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSqlException;
import io.micronaut.data.jdbc.runtime.JdbcOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EventuateMicronautDataJdbcStatementExecutor implements EventuateJdbcStatementExecutor {

  private JdbcOperations jdbcOperations;

  public EventuateMicronautDataJdbcStatementExecutor(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public void update(String sql, Object... parameters) {
    Connection connection = jdbcOperations.getConnection();

    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      for (int i = 1; i <= parameters.length; i++) {
        preparedStatement.setObject(i, parameters[i - 1]);
      }

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      throw new EventuateSqlException(e);
    }
  }
}
