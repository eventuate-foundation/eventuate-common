package io.eventuate.common.jdbc.micronaut;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.micronaut.context.annotation.Factory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class EventuateCommonJdbcOperationsFactory {

  @Singleton
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Singleton
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(JdbcTemplate jdbcTemplate) {
    return new EventuateCommonJdbcOperations(jdbcTemplate);
  }
}
