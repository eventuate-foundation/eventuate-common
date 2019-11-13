package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class EventuateCommonJdbcOperationsConfiguration {

  @Bean
  public EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor(JdbcTemplate jdbcTemplate) {
    return jdbcTemplate::update;
  }

  @Bean
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor);
  }

  @Bean
  public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionTemplate transactionTemplate) {
    return callback -> transactionTemplate.execute(status -> {
      callback.run();
      return null;
    });
  }
}
