package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.spring.common.*;
import io.eventuate.common.jdbc.spring.sqldialect.SqlDialectConfiguration;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Import({EventuateSchemaConfiguration.class, SqlDialectConfiguration.class})
public class EventuateCommonReactiveJdbcOperationsConfiguration {

  @Bean
  public EventuateReactiveJdbcConfigurationProperties eventuateReactiveJdbcConfigurationProperties() {
    return new EventuateReactiveJdbcConfigurationProperties();
  }

  @Bean
  public EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor(EventuateReactiveConnectionFactorySelector eventuateReactiveConnectionFactorySelector) {
    return new EventuateSpringReactiveJdbcStatementExecutor(eventuateReactiveConnectionFactorySelector);
  }

  @Bean
  public EventuateTransactionTemplate eventuateTransactionTemplate(TransactionTemplate transactionTemplate) {
    return new EventuateSpringTransactionTemplate(transactionTemplate);
  }

  @Bean
  public EventuateCommonReactiveJdbcOperations eventuateCommonReactiveJdbcOperations(EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor) {
    return new EventuateCommonReactiveJdbcOperations(eventuateSpringReactiveJdbcStatementExecutor);
  }

  @Bean
  public EventuateReactiveConnectionFactorySelector eventuateReactiveConnectionFactorySelector(SqlDialectSelector sqlDialectSelector, EventuateReactiveJdbcConfigurationProperties eventuateReactiveJdbcConfigurationProperties) {
    return new EventuateReactiveConnectionFactorySelector(sqlDialectSelector, eventuateReactiveJdbcConfigurationProperties);
  }
}
