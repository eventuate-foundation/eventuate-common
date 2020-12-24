package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.SchemaAndTable;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface EventuateSqlDialect extends EventuateSqlDialectOrder {
  boolean supports(String driver);
  boolean accepts(String name);

  String getCurrentTimeInMillisecondsExpression();

  String addLimitToSql(String sql, String limitExpression);

  default String castToJson(String sqlPart,
                            EventuateSchema eventuateSchema,
                            String unqualifiedTable,
                            String column,
                            EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return sqlPart;
  }

  default String jsonColumnToString(Object object,
                                    EventuateSchema eventuateSchema,
                                    String unqualifiedTable,
                                    String column,
                                    EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return object.toString();
  }

  String getPrimaryKeyColumn(DataSource dataSource, String dataSourceUrl, SchemaAndTable schemaAndTable) throws SQLException;
}
