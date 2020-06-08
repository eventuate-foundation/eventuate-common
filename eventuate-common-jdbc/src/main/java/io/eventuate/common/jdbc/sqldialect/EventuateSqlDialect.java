package io.eventuate.common.jdbc.sqldialect;

public interface EventuateSqlDialect extends EventuateSqlDialectOrder {
  boolean supports(String driver);

  String getCurrentTimeInMillisecondsExpression();

  String addLimitToSql(String sql, String limitExpression);

  default String castToJson(String sqlPart) {
    return sqlPart;
  }

  default String objectToString(Object object) {
    return object.toString();
  }
}
