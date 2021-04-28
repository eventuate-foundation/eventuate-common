package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.jdbc.EventuateJdbcOperationsUtils;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import io.eventuate.common.spring.id.IdGeneratorConfiguration;
import io.eventuate.common.spring.jdbc.EventuateSchemaConfiguration;
import io.eventuate.common.spring.jdbc.sqldialect.SqlDialectConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.util.Optional;


@Configuration
@Import({EventuateSchemaConfiguration.class,
        SqlDialectConfiguration.class,
        EventuateCommonReactiveMysqlConfiguration.class,
        IdGeneratorConfiguration.class})
public class EventuateCommonReactiveDatabaseConfiguration {

  @Bean
  public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
    return DatabaseClient.create(connectionFactory);
  }

  @Bean
  public EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor(DatabaseClient databaseClient) {
    return new EventuateSpringReactiveJdbcStatementExecutor(databaseClient);
  }

  @Bean
  public EventuateCommonReactiveSpringJdbcOperations eventuateCommonReactiveSpringJdbcOperations(EventuateCommonReactiveDatabaseProperties eventuateCommonReactiveDatabaseProperties,
                                                                                                 SqlDialectSelector sqlDialectSelector,
                                                                                                 EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor) {

    EventuateSqlDialect eventuateSqlDialect = sqlDialectSelector.getDialect(eventuateCommonReactiveDatabaseProperties.getDriver(), Optional.empty());

    return new EventuateCommonReactiveSpringJdbcOperations(new EventuateJdbcOperationsUtils(eventuateSqlDialect),
            eventuateSpringReactiveJdbcStatementExecutor, eventuateSqlDialect);
  }

  @Bean
  public EventuateCommonReactiveDatabaseProperties eventuateCommonReactiveDatabaseProperties() {
    return new EventuateCommonReactiveDatabaseProperties();
  }

  @Bean
  public ReactiveTransactionManager reactiveTransactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }

  @Bean
  public TransactionalOperator transactionalOperator(ReactiveTransactionManager reactiveTransactionManager) {
    return TransactionalOperator.create(reactiveTransactionManager);
  }
}
