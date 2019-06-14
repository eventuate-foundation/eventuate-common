package io.eventuate.common.jdbc.sqldialect;

public interface EventuateSqlDialect {
  boolean supports(String driver);
  String getCurrentTimeInMillisecondsExpression();
  String addLimitToSql(String sql, String limitExpression);
}
