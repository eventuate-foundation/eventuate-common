package io.eventuate.common.jdbc.micronaut.data;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.micronaut.context.annotation.Factory;
import io.micronaut.data.jdbc.runtime.JdbcOperations;

import javax.inject.Singleton;

@Factory
public class EventuateJdbcMicronautDataFactory {

  @Singleton
  public EventuateJdbcStatementExecutor eventuateCommonJdbcOperations(JdbcOperations jdbcOperations) {
    return new EventuateMicronautDataJdbcStatementExecutor(jdbcOperations);
  }

  @Singleton
  public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionalExecutor transactionalExecutor) {
    return transactionalExecutor::executeInTransaction;
  }
}
