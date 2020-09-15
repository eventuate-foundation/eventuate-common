package io.eventuate.common.jdbc;


import java.util.List;
import java.util.Map;

public interface EventuateJdbcStatementExecutor {
  Long insertAndReturnGeneratedId(String sql, Object... params);
  int update(String sql, Object... params);
  <T> List<T> query(String sql, EventuateRowMapper<T> eventuateRowMapper, Object... params);
  List<Map<String, Object>> queryForList(String sql, Object... parameters);
}
