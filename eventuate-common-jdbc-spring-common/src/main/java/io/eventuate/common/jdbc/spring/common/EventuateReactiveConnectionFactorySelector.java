package io.eventuate.common.jdbc.spring.common;

import io.eventuate.common.jdbc.sqldialect.*;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

public class EventuateReactiveConnectionFactorySelector {

  private SqlDialectSelector sqlDialectSelector;
  private EventuateReactiveJdbcConfigurationProperties eventuateReactiveJdbcConfigurationProperties;

  public EventuateReactiveConnectionFactorySelector(SqlDialectSelector sqlDialectSelector,
                                                    EventuateReactiveJdbcConfigurationProperties eventuateReactiveJdbcConfigurationProperties) {
    this.sqlDialectSelector = sqlDialectSelector;
    this.eventuateReactiveJdbcConfigurationProperties = eventuateReactiveJdbcConfigurationProperties;
  }

  public ConnectionFactory select() {

    EventuateSqlDialect eventuateSqlDialect = sqlDialectSelector.getDialect(eventuateReactiveJdbcConfigurationProperties.getDriver());

    if (eventuateSqlDialect instanceof PostgresDialect) {
      return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
              .host(eventuateReactiveJdbcConfigurationProperties.getHost())
              .port(eventuateReactiveJdbcConfigurationProperties.getPort())
              .database(eventuateReactiveJdbcConfigurationProperties.getDb())
              .username(eventuateReactiveJdbcConfigurationProperties.getUser())
              .password(eventuateReactiveJdbcConfigurationProperties.getPassword())
              .build());
    }
    else {
      throw new RuntimeException("Reactive jdbc is not supported for driver: " + eventuateReactiveJdbcConfigurationProperties.getDriver());
    }
  }
}
