package io.eventuate.common.jdbc.micronaut.spring;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class EventuateJdbcMicronautSpringFactory {

  @Singleton
  public EventuateJdbcStatementExecutor eventuateCommonJdbcOperations(JdbcTemplate jdbcTemplate) {
    return jdbcTemplate::update;
  }

  @Singleton
  public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionTemplate transactionTemplate) {
    return callback -> transactionTemplate.execute(status -> {
      callback.run();
      return null;
    });
  }

  @Singleton
  @Requires(missingBeans = JdbcTemplate.class)
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Singleton
  @Requires(missingBeans = TransactionTemplate.class)
  public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
    return new TransactionTemplate(platformTransactionManager);
  }

}
