package io.eventuate.common.micronaut.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.sqldialect.*;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.util.Collection;

@Factory
//@Requires(property = "micronaut.eventuate.sql.dialect.factory", value = "true")
public class SqlDialectFactory {

  @Singleton
  public MySqlDialect mySqlDialect() {
    return new MySqlDialect();
  }

  @Singleton
  public PostgresDialect postgreSQLDialect() {
    return new PostgresDialect();
  }

  @Singleton
  public MsSqlDialect msSqlDialect() {
    return new MsSqlDialect();
  }

  @Singleton
  public DefaultEventuateSqlDialect defaultSqlDialect(@Value("${eventuate.current.time.in.milliseconds.sql:#{null}}") String customCurrentTimeInMillisecondsExpression) {
    return new DefaultEventuateSqlDialect(customCurrentTimeInMillisecondsExpression);
  }

  @Singleton
  public SqlDialectSelector sqlDialectSelector(Collection<EventuateSqlDialect> dialects) {
    return new SqlDialectSelector(dialects);
  }
}
