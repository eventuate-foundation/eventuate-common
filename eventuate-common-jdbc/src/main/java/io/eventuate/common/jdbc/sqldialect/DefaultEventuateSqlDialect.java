package io.eventuate.common.jdbc.sqldialect;

public class DefaultEventuateSqlDialect implements EventuateSqlDialect {

  private String customCurrentTimeInMillisecondsExpression;

  public DefaultEventuateSqlDialect(String customCurrentTimeInMillisecondsExpression) {
    this.customCurrentTimeInMillisecondsExpression = customCurrentTimeInMillisecondsExpression;
  }

  @Override
  public boolean supports(String driver) {
    return true;
  }

  @Override
  public String addLimitToSql(String sql, String limitExpression) {
    return String.format("%s limit %s", sql, limitExpression);
  }

  public String getCurrentTimeInMillisecondsExpression() {
    return customCurrentTimeInMillisecondsExpression;
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
