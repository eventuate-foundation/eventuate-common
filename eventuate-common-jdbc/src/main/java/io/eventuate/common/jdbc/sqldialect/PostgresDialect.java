package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import org.postgresql.util.PGobject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;

public class PostgresDialect extends AbstractEventuateSqlDialect {

  private ConcurrentMap<ColumnCacheKey, String> columnTypeCache = new ConcurrentHashMap<>();

  public PostgresDialect() {
    super(Collections.singleton("org.postgresql.Driver"),
            Collections.unmodifiableSet(new HashSet<>(asList("postgres", "postgresql", "pgsql", "pg"))),
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
                           BiFunction<String, List<Object>, List<Map<String, Object>>> selectCallback) {

    String columnType = getColumnType(eventuateSchema, unqualifiedTable, column, selectCallback);

    return "%s::%s".formatted(sqlPart, columnType);
  }

  @Override
  public String jsonColumnToString(Object object,
                                   EventuateSchema eventuateSchema,
                                   String unqualifiedTable,
                                   String column,
                                   EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {

    if (object instanceof String string) return string;

    if (object instanceof PGobject pGobject) {

      if ("json".equals(pGobject.getType())) {
        return pGobject.getValue();
      }

      throw new IllegalArgumentException("Unsupported postgres type %s of column %s".formatted(pGobject.getType(), column));
    }

    throw new IllegalArgumentException("Unsupported java type %s for column %s".formatted(object.getClass(), column));
  }

  public String getColumnType(EventuateSchema eventuateSchema,
                                String unqualifiedTable,
                                String column,
                                BiFunction<String, List<Object>, List<Map<String, Object>>> selectCallback) {

    return columnTypeCache.computeIfAbsent(
            new ColumnCacheKey(eventuateSchema.getEventuateDatabaseSchema(), unqualifiedTable, column),
            columnCacheKey -> {
              final String sql =
                      "select data_type " +
                      "from information_schema.columns " +
                      "where table_schema = ? and table_name = ? and column_name = ?";

              List<Object> queryArgs = asList(eventuateSchema.isEmpty() ? "public" : eventuateSchema.getEventuateDatabaseSchema(), unqualifiedTable, column);
              List<Map<String, Object>> results = selectCallback.apply(sql, queryArgs);
              if (results.isEmpty())
                throw new RuntimeException("Could not retrieve metadata for " + queryArgs);
              return (String) results.get(0).get("data_type");
            });
  }

  @Override
  public String addReturningOfGeneratedIdToSql(String sql, String idColumn) {
    return "%s RETURNING %s".formatted(sql, idColumn);
  }
}
