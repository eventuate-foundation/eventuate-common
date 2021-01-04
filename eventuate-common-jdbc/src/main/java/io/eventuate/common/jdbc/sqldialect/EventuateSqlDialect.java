package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.JdbcUrlParser;
import io.eventuate.common.jdbc.SchemaAndTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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

  default String jsonColumnToString(Object object,
                                    EventuateSchema eventuateSchema,
                                    String unqualifiedTable,
                                    String column,
                                    EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    return object.toString();
  }

  default String getJdbcCatalogue(String dataSourceUrl) {
    return null;
  }

  default String getPrimaryKeyColumn(DataSource dataSource, String dataSourceUrl, SchemaAndTable schemaAndTable) throws SQLException {
    String pk;
    Connection connection = null;
    try {
      connection = dataSource.getConnection();

      ResultSet resultSet = connection
              .getMetaData()
              .getPrimaryKeys(getJdbcCatalogue(dataSourceUrl),
                      schemaAndTable.getSchema(),
                      schemaAndTable.getTableName());

      if (resultSet.next()) {
        pk = resultSet.getString("COLUMN_NAME");
        if (resultSet.next()) {
          throw new RuntimeException(String.format("Table %s has more than one primary key", schemaAndTable));
        }
      } else {
        throw new RuntimeException(String.format("Cannot get primary key of table %s: result set is empty", schemaAndTable));
      }
    } finally {
      if (connection != null) {
        connection.close();
      }
    }

    return pk;
  }
}
