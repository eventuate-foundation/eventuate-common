package io.eventuate.common.jdbc.sqldialect;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDialectTest {
  private SqlDialectSelector sqlDialectSelector;

  private String name;
  private String driver;
  private Class<? extends EventuateSqlDialect> expectedDialectClass;
  private String expectedCurrentTimeInMillisecondsExpression;


  public AbstractDialectTest(String name,
                             String driver,
                             Class<? extends EventuateSqlDialect> expectedDialectClass,
                             String expectedCurrentTimeInMillisecondsExpression,
                             Optional<String> customCurrentTimeInMillisecondsExpression) {
    this.name = name;
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
  public void testDialectSelectionByDriver() {
    Assert.assertEquals(expectedDialectClass, getDialectByDriver().getClass());

    Assert.assertEquals(expectedCurrentTimeInMillisecondsExpression,
            getDialectByDriver().getCurrentTimeInMillisecondsExpression());
  }

  @Test
  public void testDialectSelectionByName() {
    Assert.assertEquals(expectedDialectClass, getDialectByName().getClass());

    Assert.assertEquals(expectedCurrentTimeInMillisecondsExpression,
            getDialectByName().getCurrentTimeInMillisecondsExpression());
  }

  protected EventuateSqlDialect getDialectByDriver() {
    return sqlDialectSelector.getDialect(driver);
  }

  protected EventuateSqlDialect getDialectByNameAndDriver() {
    return sqlDialectSelector.getDialect("other", Optional.of(driver));
  }

  protected EventuateSqlDialect getDialectByName() {
    return sqlDialectSelector.getDialect(name, Optional.empty());
  }
}
