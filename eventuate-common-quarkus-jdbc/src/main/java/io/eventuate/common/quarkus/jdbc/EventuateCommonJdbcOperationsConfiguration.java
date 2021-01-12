package io.eventuate.common.quarkus.jdbc;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateCommonJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSqlException;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import io.quarkus.arc.DefaultBean;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

@ApplicationScoped
public class EventuateCommonJdbcOperationsConfiguration {

  @Produces
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                                                     SqlDialectSelector sqlDialectSelector,
                                                                     @ConfigProperty(name = "eventuateDatabase") String dbName) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor, sqlDialectSelector.getDialect(dbName, Optional.empty()));
  }

  @Produces
  public EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor(Instance<DataSource> dataSource) {
    return new EventuateCommonJdbcStatementExecutor(() -> {
      try {
        return dataSource.get().getConnection();
      } catch (SQLException e) {
        throw new EventuateSqlException(e);
      }
    });
  }

  @Produces
  public EventuateTransactionTemplate eventuateTransactionTemplate(EventuateQuarkusTransactionTemplate eventuateQuarkusTransactionTemplate) {
    return eventuateQuarkusTransactionTemplate::executeInTransaction;
  }
}
