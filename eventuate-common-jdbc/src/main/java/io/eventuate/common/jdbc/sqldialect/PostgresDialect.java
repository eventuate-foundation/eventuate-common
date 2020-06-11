package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import org.postgresql.util.PGobject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PostgresDialect extends DefaultEventuateSqlDialect {

  private ConcurrentMap<ColumnCacheKey, String> columnTypeCache = new ConcurrentHashMap<>();

  public PostgresDialect() {
    super("(ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000))");
  }

  @Override
  public boolean supports(String driver) {
    return "org.postgresql.Driver".equals(driver);
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }

  @Override
  public String castToJson(String sqlPart,
                           EventuateSchema eventuateSchema,
                           String unqualifiedTable,
                           String column,
                           EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {

    String columnType = getColumnType(eventuateSchema, unqualifiedTable, column, eventuateJdbcStatementExecutor);

    return String.format("%s::%s", sqlPart, columnType);
  }

  @Override
  public String jsonColumnToString(Object object,
                                   EventuateSchema eventuateSchema,
                                   String unqualifiedTable,
                                   String column,
                                   EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {

    if (object instanceof String) return (String) object;

    if (object instanceof PGobject) {
      PGobject pGobject = (PGobject) object;

      if ("json".equals(pGobject.getType())) {
        return pGobject.getValue();
      }

      throw new IllegalArgumentException(String.format("Unsupported postgres type %s of column %s", pGobject.getType(), column));
    }

    throw new IllegalArgumentException(String.format("Unsupported java type %s for column %s", object.getClass(), column));
  }

  private String getColumnType(EventuateSchema eventuateSchema,
                               String unqualifiedTable,
                               String column,
                               EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {

    return columnTypeCache.computeIfAbsent(
            new ColumnCacheKey(eventuateSchema.getEventuateDatabaseSchema(), unqualifiedTable, column),
            columnCacheKey -> {
              String informationSchema = eventuateSchema.qualifyTable("information_schema");

              final String sql = String
                      .format("select data_type from %s.columns where table_name = ? and column_name = ?", informationSchema);

              return (String) eventuateJdbcStatementExecutor
                      .queryForList(sql, unqualifiedTable, column)
                      .get(0)
                      .get("data_type");
            });
  }
}
