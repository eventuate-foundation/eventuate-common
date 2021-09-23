package io.eventuate.common.spring.jdbc.reactive;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.eventuate.common.reactive.jdbc.EventuateReactiveDatabases.POSTGRES;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
public class EventuateCommonReactivePostgresConfiguration {

  @Bean
  @ConditionalOnProperty(name = "eventuate.reactive.db.driver", havingValue = POSTGRES)
  public ConnectionFactory postgresConnectionFactory(EventuateCommonReactiveDatabaseProperties eventuateCommonReactiveDatabaseProperties) {
    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
            .option(DRIVER, eventuateCommonReactiveDatabaseProperties.getDriver())
            .option(HOST, eventuateCommonReactiveDatabaseProperties.getHost())
            .option(USER, eventuateCommonReactiveDatabaseProperties.getUsername())
            .option(PORT, eventuateCommonReactiveDatabaseProperties.getPort())
            .option(PASSWORD, eventuateCommonReactiveDatabaseProperties.getPassword())
            .option(DATABASE, eventuateCommonReactiveDatabaseProperties.getDatabase())
            .option(Option.valueOf("tcpKeepAlive"), true)
            .build();

    return ConnectionFactories.get(options);
  }
}
