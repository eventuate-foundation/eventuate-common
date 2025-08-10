package io.eventuate.common.jdbc.sqldialect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class UnknownSqlDialectTest {

  @Test
  public void testDialectSelectionByDriver() {
    IllegalStateException exception = null;

    try {
      new SqlDialectSelector(Collections.emptySet()).getDialect("unknown.UnknownDriver");
    } catch (IllegalStateException e) {
      exception = e;
    }

    Assertions.assertNotNull(exception);

    String expectedMessage = "Sql Dialect not found (unknown.UnknownDriver), " +
            "you can specify environment variable 'EVENTUATE_CURRENT_TIME_IN_MILLISECONDS_SQL' to solve the issue";

    Assertions.assertEquals(expectedMessage, exception.getMessage());
  }
}
