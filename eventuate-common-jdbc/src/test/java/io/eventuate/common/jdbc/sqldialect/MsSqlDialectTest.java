package io.eventuate.common.jdbc.sqldialect;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MsSqlDialectTest extends AbstractDialectTest {

  public MsSqlDialectTest() {
    super("mssql",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            MsSqlDialect.class,
            "(SELECT DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE()))",
            Optional.empty());
  }

  @Test
  public void shouldReplaceSelectRegardlessOfCase() {
    assertEquals("select top (:limit) * FROM FOO", getDialectByDriver().addLimitToSql("sELeCt * FROM FOO", ":limit"));
  }

  @Test
  public void shouldFailIfNotReplace() {
    assertThrows(IllegalArgumentException.class, () ->
      getDialectByDriver().addLimitToSql("UPDATE FOO ...", ":limit"));
  }
}
