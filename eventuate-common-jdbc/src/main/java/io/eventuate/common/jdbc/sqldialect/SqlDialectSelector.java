package io.eventuate.common.jdbc.sqldialect;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class SqlDialectSelector {
  private Collection<EventuateSqlDialect> sqlDialects;

  public SqlDialectSelector(Collection<EventuateSqlDialect> sqlDialects) {
    this.sqlDialects = sqlDialects;
  }

  public EventuateSqlDialect getDialect(String driver) {
    return getDialect(dialect -> dialect.supports(driver), driver);
  }

  public EventuateSqlDialect getDialect(String name, Optional<String> driver) {

    String failMessage = name.concat(driver.map("/"::concat).orElse(""));

    return getDialect(dialect -> driver.map(dialect::supports).orElse(false) || dialect.accepts(name), failMessage);

  }

  private EventuateSqlDialect getDialect(Predicate<EventuateSqlDialect> predicate, String searchFailedMessage) {
    return sqlDialects
            .stream()
            .filter(predicate)
            .min(Comparator.comparingInt(EventuateSqlDialectOrder::getOrder))
            .orElseThrow(() ->
                    new IllegalStateException(String.format("Sql Dialect not found (%s), " +
                                    "you can specify environment variable '%s' to solve the issue",
                            searchFailedMessage,
                            "EVENTUATE_CURRENT_TIME_IN_MILLISECONDS_SQL")));
  }
}
