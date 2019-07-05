package io.eventuate.common.jdbc.micronaut;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class EventuateCommonJdbcOperationsFactory {

  @Singleton
  public EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor(EventuateMicronautTransactionManagement eventuateMicronautTransactionManagement) {
    return new EventuateMicronautJdbcStatementExecutor(eventuateMicronautTransactionManagement);
  }

  @Singleton
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor);
  }
}
