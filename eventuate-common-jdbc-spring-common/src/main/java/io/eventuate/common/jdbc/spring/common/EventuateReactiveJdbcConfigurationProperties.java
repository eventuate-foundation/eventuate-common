package io.eventuate.common.jdbc.spring.common;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class EventuateReactiveJdbcConfigurationProperties {
  @Value("${spring.datasource.username}")
  private String user;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.driver.class.name}")
  private String driver;

  private String host;
  private int port;
  private String db;


  @PostConstruct
  public void init() {
    JdbcUrl jdbcUrl = JdbcUrlParser.parse(url);

    host = jdbcUrl.getHost();
    port = jdbcUrl.getPort();
    db = jdbcUrl.getDatabase();
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getUrl() {
    return url;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getDb() {
    return db;
  }

  public String getDriver() {
    return driver;
  }
}
