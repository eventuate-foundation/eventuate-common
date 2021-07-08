package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import org.postgresql.util.PGobject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PostgresDialect extends AbstractEventuateSqlDialect {

  private ConcurrentMap<ColumnCacheKey, String> columnTypeCache = new ConcurrentHashMap<>();

  public PostgresDialect() {
    super(Collections.singleton("org.postgresql.Driver"),
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("postgres", "postgresql", "pgsql", "pg"))),
            "(ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000))");
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
              String sql = "select data_type from information_schema.columns where table_schema = ? and table_name = ? and column_name = ?";

              return (String) eventuateJdbcStatementExecutor
                      .queryForList(sql, eventuateSchema.getEventuateDatabaseSchema(), unqualifiedTable, column)
                      .get(0)
                      .get("data_type");
            });
  }
}
