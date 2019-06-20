package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

public class EventuateSpringJdbcStatementExecutor implements EventuateJdbcStatementExecutor {

  private JdbcTemplate jdbcTemplate;

  public EventuateSpringJdbcStatementExecutor(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void update(String sql, Object... params) {
    jdbcTemplate.update(sql, params);
  }
}
