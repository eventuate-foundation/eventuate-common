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
                                BiFunction<String, List<Object>, List<Map<String, Object>>> selectCallback) {

    return columnTypeCache.computeIfAbsent(
            new ColumnCacheKey(eventuateSchema.getEventuateDatabaseSchema(), unqualifiedTable, column),
            columnCacheKey -> {
              String informationSchema = eventuateSchema.qualifyTable("information_schema");

              final String sql = String
                      .format("select data_type from %s.columns where table_name = ? and column_name = ?", informationSchema);

              return (String) selectCallback.apply(sql, asList(unqualifiedTable, column))
                      .get(0)
                      .get("data_type");
            });
  }

  @Override
  public String addReturningOfGeneratedIdToSql(String sql, String idColumn) {
    return String.format("%s RETURNING %s", sql, idColumn);
  }
}
