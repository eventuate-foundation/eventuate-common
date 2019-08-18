package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class EventuateCommonJdbcOperationsConfiguration {

  @Bean
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(JdbcTemplate jdbcTemplate) {
    return new EventuateCommonJdbcOperations(jdbcTemplate);
  }
}
