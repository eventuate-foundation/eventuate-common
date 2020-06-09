package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;

public interface EventuateSqlDialect extends EventuateSqlDialectOrder {
  boolean supports(String driver);

  String getCurrentTimeInMillisecondsExpression();

  String addLimitToSql(String sql, String limitExpression);

  default String castToJson(String sqlPart,
                            EventuateSchema eventuateSchema,
                            String unqualifiedTable,
                            String column,
                            EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return sqlPart;
  }

  default String objectToString(Object object,
                                EventuateSchema eventuateSchema,
                                String unqualifiedTable,
                                String column,
                                EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return object.toString();
  }
}
