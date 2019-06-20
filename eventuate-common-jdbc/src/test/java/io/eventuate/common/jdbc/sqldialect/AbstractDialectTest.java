package io.eventuate.common.jdbc.sqldialect;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDialectTest {
  private SqlDialectSelector sqlDialectSelector;

  private String driver;
  private Class<? extends EventuateSqlDialect> expectedDialectClass;
  private String expectedCurrentTimeInMillisecondsExpression;


  public AbstractDialectTest(String driver,
                             Class<? extends EventuateSqlDialect> expectedDialectClass,
                             String expectedCurrentTimeInMillisecondsExpression,
                             Optional<String> customCurrentTimeInMillisecondsExpression) {
    this.driver = driver;
    this.expectedDialectClass = expectedDialectClass;
    this.expectedCurrentTimeInMillisecondsExpression = expectedCurrentTimeInMillisecondsExpression;

    List<EventuateSqlDialect> dialects = new ArrayList<>();

    dialects.add(new MySqlDialect());
    dialects.add(new PostgresDialect());
    dialects.add(new MsSqlDialect());
    dialects.add(new DefaultEventuateSqlDialect(customCurrentTimeInMillisecondsExpression.orElse(null)));

    sqlDialectSelector = new SqlDialectSelector(dialects);
  }

  @Test
  public void testDialect() {
    Assert.assertEquals(expectedDialectClass, sqlDialectSelector.getDialect(driver).getClass());

    Assert.assertEquals(expectedCurrentTimeInMillisecondsExpression,
            sqlDialectSelector.getDialect(driver).getCurrentTimeInMillisecondsExpression());
  }
}
