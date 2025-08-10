package io.eventuate.common.micronaut.inmemorydatabase;

import io.eventuate.common.inmemorydatabase.EmbeddedDatabaseBuilder;
import io.eventuate.common.inmemorydatabase.EventuateDatabaseScriptSupplier;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.micronaut.context.annotation.Context;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Context
public class EventuateCommonInMemoryDatabaseFactory {

  EventuateDatabaseScriptSupplier[] scripts;
  EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;
  EventuateTransactionTemplate eventuateTransactionTemplate;

  public EventuateCommonInMemoryDatabaseFactory(EventuateDatabaseScriptSupplier[] scripts,
                                                EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                                EventuateTransactionTemplate eventuateTransactionTemplate) {
    this.scripts = scripts;
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
    this.eventuateTransactionTemplate = eventuateTransactionTemplate;
  }

  @PostConstruct
  public void dataSource() {
    new EmbeddedDatabaseBuilder(Arrays.stream(scripts), eventuateJdbcStatementExecutor, eventuateTransactionTemplate).build();
  }
}
