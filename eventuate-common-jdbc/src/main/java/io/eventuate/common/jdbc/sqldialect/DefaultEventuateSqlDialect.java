package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.SchemaAndTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
  public String getPrimaryKeyColumn(DataSource dataSource, String dataSourceUrl, SchemaAndTable schemaAndTable) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      ResultSet resultSet = connection
              .getMetaData()
              .getPrimaryKeys(getJdbcCatalogue(dataSourceUrl),
                      schemaAndTable.getSchema(),
                      schemaAndTable.getTableName());

      if (resultSet.next()) {
        String pk = resultSet.getString("COLUMN_NAME");

        if (resultSet.next()) {
          throw new RuntimeException(String.format("Table %s has more than one primary key", schemaAndTable));
        }

        return pk;
      } else {
        throw new RuntimeException(String.format("Cannot get primary key of table %s: result set is empty", schemaAndTable));
      }
    }
  }

  protected String getJdbcCatalogue(String dataSourceUrl) {
    return null;
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
