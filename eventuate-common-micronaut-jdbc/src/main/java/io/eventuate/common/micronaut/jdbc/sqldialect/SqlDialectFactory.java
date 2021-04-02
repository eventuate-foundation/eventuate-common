package io.eventuate.common.micronaut.jdbc.sqldialect;

import io.eventuate.common.jdbc.sqldialect.*;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Collection;

@Factory
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
  public DefaultEventuateSqlDialect defaultSqlDialect(@Nullable @Value("${eventuate.current.time.in.milliseconds.sql}") String customCurrentTimeInMillisecondsExpression) {
    if (customCurrentTimeInMillisecondsExpression == null) {
      customCurrentTimeInMillisecondsExpression = "null";
    }

    return new DefaultEventuateSqlDialect(customCurrentTimeInMillisecondsExpression);
  }

  @Singleton
  public SqlDialectSelector sqlDialectSelector(Collection<EventuateSqlDialect> dialects) {
    return new SqlDialectSelector(dialects);
  }
}
