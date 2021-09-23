package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.PostgresDialect;
import io.eventuate.common.reactive.jdbc.EventuateReactiveJdbcStatementExecutor;
import io.eventuate.common.reactive.jdbc.EventuateReactiveRowMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class EventuateSpringReactiveJdbcStatementExecutor implements EventuateReactiveJdbcStatementExecutor {
  private DatabaseClient databaseClient;
  private EventuateSqlDialect sqlDialect;

  public EventuateSpringReactiveJdbcStatementExecutor(DatabaseClient databaseClient, EventuateSqlDialect sqlDialect) {
    this.databaseClient = databaseClient;
    this.sqlDialect = sqlDialect;
  }

  public Mono<Integer> update(String sql, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);

    return genericExecuteSpec.fetch().rowsUpdated().doOnError(this::handleDuplicateKeyException);
  }

  public Mono<Long> insertAndReturnId(String sql, String idColumn, Object... params) {
    sql = sqlDialect.addReturningOfGeneratedIdToSql(reformatInsertParameters(sql, params, params), idColumn);

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);

    return genericExecuteSpec
            .fetch()
            .one()
            .doOnError(this::handleDuplicateKeyException)
            .map(m -> {
              Object value = m.values().stream().findFirst().get();

              if (value instanceof Long) return (Long)value;
              if (value instanceof BigInteger) return ((BigInteger)value).longValue();

              throw new IllegalStateException();
            });
  }

//    Following code does not work properly because of bug in r2dbc, but should work in 0.8.3. See:
//    https://github.com/mirromutth/r2dbc-mysql/issues/149
//    https://github.com/mirromutth/r2dbc-mysql/pull/159
//
//  public Flux<Map<String, Object>> query(String sql, Object... params) {
//    sql = reformatParameters(sql, params);
//
//    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);
//
//    return genericExecuteSpec.fetch().all();
//  }

  public Flux<Map<String, Object>> query(String sql, Object... params) {
    return queryForList(sql, (row, rowMetadata) -> {
      Map<String, Object> result = new HashMap<>();

      rowMetadata.getColumnNames().forEach(name -> result.put(name, row.get(name)));

      return result;
    }, params);
  }

  public <T> Flux<T> queryForList(String sql, EventuateReactiveRowMapper<T> eventuateReactiveRowMapper, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);

    return genericExecuteSpec.map(eventuateReactiveRowMapper).all();
  }

  private String reformatParameters(String sql, Object[] params) {
    for (int i = 1; i <= params.length; i++) {
      sql = sql.replaceFirst("\\?", ":param" + i);
    }

    return sql;
  }

  private String reformatInsertParameters(String sql, Object[] params, Object[] values) {
    for (int i = 1; i <= params.length; i++) {
      //Hack for postgres, null binding does not work: [CIRCULAR REFERENCE:java.lang.IllegalArgumentException: Cannot encode null parameter of type java.lang.Object]
      if (sqlDialect instanceof PostgresDialect && values[i - 1] == null) {
        sql = sql.replaceFirst("\\?", "NULL");
      } else {
        sql = sql.replaceFirst("\\?", ":param" + i);
      }
    }

    return sql;
  }

  private DatabaseClient.GenericExecuteSpec bindParameters(DatabaseClient.GenericExecuteSpec genericExecuteSpec, Object[] params) {
    for (int i = 0; i < params.length; i++) {
      if (params[i] == null) {
        if (!(sqlDialect instanceof PostgresDialect)) {
          genericExecuteSpec = genericExecuteSpec.bindNull(i, Object.class);
        }
      }
      else {
        genericExecuteSpec = genericExecuteSpec.bind(i, params[i]);
      }
    }

    return genericExecuteSpec;
  }

  private void handleDuplicateKeyException(Throwable throwable) {
    if (throwable.getMessage().contains("Duplicate entry") || throwable.getMessage().contains("duplicate key")) {
      throw new EventuateDuplicateKeyException(throwable);
    }

    throw new RuntimeException(throwable);
  }
}
