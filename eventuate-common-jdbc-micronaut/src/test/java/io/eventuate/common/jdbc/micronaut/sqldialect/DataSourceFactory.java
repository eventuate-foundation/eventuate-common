package io.eventuate.common.jdbc.micronaut.sqldialect;

import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class DataSourceFactory {

  @Value("${micronaut.datasource.url}")
  private String url;

  @Value("${micronaut.datasource.username}")
  private String user;

  @Value("${micronaut.datasource.password}")
  private String password;

  @Value("${micronaut.datasource.driver.class.name}")
  private String driver;

  @Singleton
  public DataSource dataSource() {
    HikariDataSource hikariDataSource = new HikariDataSource();

    hikariDataSource.setUsername(user);
    hikariDataSource.setPassword(password);
    hikariDataSource.setJdbcUrl(url);
    hikariDataSource.setDriverClassName(driver);

    hikariDataSource.setConnectionTestQuery("select 1");

    return hikariDataSource;
  }
}
