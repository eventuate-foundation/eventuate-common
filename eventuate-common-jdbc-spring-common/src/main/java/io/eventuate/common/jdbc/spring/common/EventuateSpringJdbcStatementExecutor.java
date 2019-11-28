package io.eventuate.common.jdbc.spring.common;

import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateRowMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class EventuateSpringJdbcStatementExecutor implements EventuateJdbcStatementExecutor  {

  private JdbcTemplate jdbcTemplate;

  public EventuateSpringJdbcStatementExecutor(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int update(String sql, Object... params) {
    try {
      return jdbcTemplate.update(sql, params);
    } catch (DuplicateKeyException e) {
      throw new EventuateDuplicateKeyException(e);
    }
  }

  @Override
  public <T> List<T> query(String sql, EventuateRowMapper<T> eventuateRowMapper, Object... params) {
    return jdbcTemplate.query(sql, eventuateRowMapper::mapRow, params);
  }

  @Override
  public List<Map<String, Object>> queryForList(String sql, Object... parameters) {
    return jdbcTemplate.queryForList(sql, parameters);
  }
}
