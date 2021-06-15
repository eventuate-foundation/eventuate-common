package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.reactive.jdbc.EventuateReactiveJdbcStatementExecutor;
import io.eventuate.common.reactive.jdbc.EventuateReactiveRowMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
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

  public Flux<Map<String, Object>> query(String sql, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(databaseClient.sql(sql), params);

    return genericExecuteSpec.fetch().all();
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
