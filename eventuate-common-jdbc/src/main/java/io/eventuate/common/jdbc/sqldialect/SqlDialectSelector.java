package io.eventuate.common.jdbc.sqldialect;

import java.util.Collection;
import java.util.Comparator;

public class SqlDialectSelector {
  private Collection<EventuateSqlDialect> sqlDialects;

  public SqlDialectSelector(Collection<EventuateSqlDialect> sqlDialects) {
    this.sqlDialects = sqlDialects;
  }

  public EventuateSqlDialect getDialect(String driver) {
    return sqlDialects
            .stream()
            .filter(dialect -> dialect.supports(driver))
            .min(Comparator.comparingInt(EventuateSqlDialectOrder::getOrder))
            .orElseThrow(() ->
                    new IllegalStateException(String.format("Sql Dialect not found (%s), " +
                                    "you can specify environment variable '%s' to solve the issue",
                            driver,
                            "EVENTUATE_CURRENT_TIME_IN_MILLISECONDS_SQL")));
  }
}
