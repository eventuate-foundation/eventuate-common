package io.eventuate.common.jdbc;


public interface EventuateJdbcStatementExecutor {
  void update(String sql, Object... params);
}
