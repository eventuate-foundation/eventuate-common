package io.eventuate.common.jdbc.spring.common;

import io.eventuate.common.jdbc.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.*;

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
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof SQLException) {
        SQLException sqlException = (SQLException) e.getCause();
        if (EventuateJdbcUtils.isDuplicateKeyException(sqlException.getSQLState(), sqlException.getErrorCode())) {
          throw new EventuateDuplicateKeyException(e);
        }
      }

      throw new EventuateSqlException(e);
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
