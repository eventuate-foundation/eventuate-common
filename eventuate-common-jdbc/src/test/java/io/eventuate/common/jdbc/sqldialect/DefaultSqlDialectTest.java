package io.eventuate.common.jdbc.sqldialect;

import java.util.Optional;

public class DefaultSqlDialectTest extends AbstractDialectTest {

  public DefaultSqlDialectTest() {
    super("other", "no.Matter",
            DefaultEventuateSqlDialect.class,
            "some custom sql",
            Optional.of("some custom sql"));
  }
}
