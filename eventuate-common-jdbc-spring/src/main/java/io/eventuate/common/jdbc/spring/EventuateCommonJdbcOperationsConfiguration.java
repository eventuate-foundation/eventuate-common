package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.spring.common.EventuateSpringJdbcStatementExecutor;
import io.eventuate.common.jdbc.spring.common.EventuateSpringTransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Import(EventuateSchemaConfiguration.class)
public class EventuateCommonJdbcOperationsConfiguration {

  @Bean
  public EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor(JdbcTemplate jdbcTemplate) {
    return new EventuateSpringJdbcStatementExecutor(jdbcTemplate);
  }

  @Bean
  public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionTemplate transactionTemplate) {
    return new EventuateSpringTransactionTemplate(transactionTemplate);
  }

  @Bean
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor);
  }
}
