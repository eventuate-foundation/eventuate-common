package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.SchemaAndTable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface EventuateSqlDialect extends EventuateSqlDialectOrder {
  boolean supports(String driver);
  boolean accepts(String name);

  String getCurrentTimeInMillisecondsExpression();

  String addLimitToSql(String sql, String limitExpression);

  String addReturningOfGeneratedIdToSql(String sql, String idColumn);

  default String castToJson(String sqlPart,
                            EventuateSchema eventuateSchema,
                            String unqualifiedTable,
                            String column,
                            BiFunction<String, List<Object>, List<Map<String, Object>>> selectCallback) {
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
