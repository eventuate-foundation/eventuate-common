package io.eventuate.common.quarkus.jdbc.sqldialect;

import io.eventuate.common.jdbc.sqldialect.DefaultEventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.MsSqlDialect;
import io.eventuate.common.jdbc.sqldialect.MySqlDialect;
import io.eventuate.common.jdbc.sqldialect.PostgresDialect;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class SqlDialectConfiguration {

  @Produces
  public MySqlDialect mySqlDialect() {
    return new MySqlDialect();
  }

  @Produces
  public PostgresDialect postgreSQLDialect() {
    return new PostgresDialect();
  }

  @Produces
  public MsSqlDialect msSqlDialect() {
    return new MsSqlDialect();
  }

  @Produces
  public DefaultEventuateSqlDialect defaultSqlDialect(@ConfigProperty(name = "eventuate.current.time.in.milliseconds.sql")
                                                              Optional<String> customCurrentTimeInMillisecondsExpression) {
    return new DefaultEventuateSqlDialect(customCurrentTimeInMillisecondsExpression.orElse(null));
  }

  @Produces
  public SqlDialectSelector sqlDialectSelector(Instance<EventuateSqlDialect> dialects) {
    return new SqlDialectSelector(dialects.stream().collect(Collectors.toList()));
  }
}
