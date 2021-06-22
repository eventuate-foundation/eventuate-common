package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
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

  public EventuateSpringReactiveJdbcStatementExecutor(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  public Mono<Integer> update(String sql, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);

    return genericExecuteSpec.fetch().rowsUpdated().doOnError(this::handleDuplicateKeyException);
  }

  public Mono<Long> insertAndReturnId(String sql, String idColumn, Object... params) {
    sql = String.format("%s%s", reformatParameters(sql, params), ";SELECT LAST_INSERT_ID();");

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);

    return genericExecuteSpec
            .fetch()
            .one()
            .doOnError(this::handleDuplicateKeyException)
            .map(m -> ((BigInteger)(m.get("LAST_INSERT_ID()"))).longValue());
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

  private DatabaseClient.GenericExecuteSpec bindParameters(DatabaseClient.GenericExecuteSpec genericExecuteSpec, Object[] params) {
    for (int i = 0; i < params.length; i++) {
      genericExecuteSpec = genericExecuteSpec.bind(i, params[i]);
    }

    return genericExecuteSpec;
  }

  private void handleDuplicateKeyException(Throwable throwable) {
    if (throwable.getMessage().contains("Duplicate entry")) {
      throw new EventuateDuplicateKeyException(throwable);
    }

    throw new RuntimeException(throwable);
  }
}
