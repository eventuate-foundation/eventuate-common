package io.eventuate.common.reactive.jdbc;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface EventuateReactiveJdbcStatementExecutor {
  Mono<Long> update(String sql, Object... params);
  Mono<Long> insertAndReturnId(String sql, String idColumn, Object... params);
  Flux<Map<String, Object>> query(String sql, Object... params);
  <T> Flux<T> queryForList(String sql, EventuateReactiveRowMapper<T> eventuateReactiveRowMapper, Object... params);
}
