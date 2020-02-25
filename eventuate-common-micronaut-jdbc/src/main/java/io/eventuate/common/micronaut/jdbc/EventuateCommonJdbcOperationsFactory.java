package io.eventuate.common.micronaut.jdbc;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import javax.inject.Singleton;
import java.util.function.Supplier;

@Factory
public class EventuateCommonJdbcOperationsFactory {

  @Singleton
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor);
  }

  @Singleton
  @Requires(missingBeans = EventuateTransactionTemplate.class)
  public EventuateTransactionTemplate eventuateTransactionTemplate() {
    return new EventuateTransactionTemplate() {
      @Override
      public <T> T executeInTransaction(Supplier<T> callback) {
        return callback.get();
      }
    };
  }
}
