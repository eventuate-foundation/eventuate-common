package io.eventuate.common.jdbc.sqldialect;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MsSqlDialectTest extends AbstractDialectTest {

  public MsSqlDialectTest() {
    super("com.microsoft.sqlserver.jdbc.SQLServerDriver",
            MsSqlDialect.class,
            "(SELECT DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE()))",
            Optional.empty());
  }

  @Test
  public void shouldReplaceSelectRegardlessOfCase() {
    assertEquals("select top (:limit) * FROM FOO", getDialect().addLimitToSql("sELeCt * FROM FOO", ":limit"));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldFailIfNotReplace() {
    getDialect().addLimitToSql("UPDATE FOO ...", ":limit");
  }
}
