package io.eventuate.common.jdbc.micronaut;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import javax.inject.Singleton;

@Factory
public class EventuateCommonJdbcOperationsFactory {

  @Singleton
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor);
  }

  @Singleton
  @Requires(missingBeans = EventuateTransactionTemplate.class)
  public EventuateTransactionTemplate eventuateTransactionTemplate() {
    return Runnable::run;
  }
}
