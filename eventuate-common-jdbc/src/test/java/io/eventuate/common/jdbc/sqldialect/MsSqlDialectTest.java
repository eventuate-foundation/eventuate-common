package io.eventuate.common.jdbc.sqldialect;

import java.util.Optional;

public class MsSqlDialectTest extends AbstractDialectTest {

  public MsSqlDialectTest() {
    super("com.microsoft.sqlserver.jdbc.SQLServerDriver",
            MsSqlDialect.class,
            "(SELECT DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE()))",
            Optional.empty());
  }
}
