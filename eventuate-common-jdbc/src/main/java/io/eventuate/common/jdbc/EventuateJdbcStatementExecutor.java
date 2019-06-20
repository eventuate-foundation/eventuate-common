package io.eventuate.common.jdbc;

import java.sql.SQLException;

public interface EventuateJdbcStatementExecutor {
  void update(String sql, Object... params) throws SQLException;
}
