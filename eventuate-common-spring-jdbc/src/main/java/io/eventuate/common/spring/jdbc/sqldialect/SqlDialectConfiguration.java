package io.eventuate.common.spring.jdbc.sqldialect;

import io.eventuate.common.jdbc.sqldialect.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
public class SqlDialectConfiguration {

  @Bean
  public MySqlDialect mySqlDialect() {
    return new MySqlDialect();
  }

  @Bean
  public PostgresDialect postgreSQLDialect() {
    return new PostgresDialect();
  }

  @Bean
  public MsSqlDialect msSqlDialect() {
    return new MsSqlDialect();
  }

  @Bean
  public DefaultEventuateSqlDialect defaultSqlDialect(@Value("${eventuate.current.time.in.milliseconds.sql:#{null}}") String customCurrentTimeInMillisecondsExpression) {
    return new DefaultEventuateSqlDialect(customCurrentTimeInMillisecondsExpression);
  }

  @Bean
  public SqlDialectSelector sqlDialectSelector(Collection<EventuateSqlDialect> dialects) {
    return new SqlDialectSelector(dialects);
  }
}
