package io.eventuate.common.micronaut.jdbc;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcOperationsUtils;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.util.function.Supplier;

@Factory
public class EventuateCommonJdbcOperationsFactory {

  @Singleton
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                                                     SqlDialectSelector sqlDialectSelector,
                                                                     @Value("${datasources.default.driver-class-name}") String driver) {
    EventuateSqlDialect eventuateSqlDialect = sqlDialectSelector.getDialect(driver);

    return new EventuateCommonJdbcOperations(new EventuateJdbcOperationsUtils(eventuateSqlDialect),
            eventuateJdbcStatementExecutor, eventuateSqlDialect);
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
