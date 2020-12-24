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
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
public class EventuateCommonJdbcOperationsConfiguration {

  @Produces
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                                                     SqlDialectSelector sqlDialectSelector,
                                                                     @ConfigProperty(name = "quarkus.datasource.db-kind") String dbName,
                                                                     @ConfigProperty(name = "quarkus.datasource.jdbc.driver") Optional<String> driver) {
    return new EventuateCommonJdbcOperations(eventuateJdbcStatementExecutor, sqlDialectSelector.getDialect(dbName, driver));
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
