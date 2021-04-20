package io.eventuate.common.spring.jdbc.reactive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventuateCommonReactiveDatabaseProperties {
  @Value("${eventuate.reactive.db.driver}")
  private String driver;

  @Value("${eventuate.reactive.db.host}")
  private String host;

  @Value("${eventuate.reactive.db.port}")
  private int port;

  @Value("${eventuate.reactive.db.username}")
  private String username;

  @Value("${eventuate.reactive.db.password}")
  private String password;

  @Value("${eventuate.reactive.db.database}")
  private String database;

  public String getDriver() {
    return driver;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getDatabase() {
    return database;
  }
}
