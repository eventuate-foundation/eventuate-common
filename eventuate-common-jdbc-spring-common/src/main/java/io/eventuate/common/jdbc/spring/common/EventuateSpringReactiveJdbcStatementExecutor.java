package io.eventuate.common.jdbc.spring.common;

import io.eventuate.common.jdbc.EventuateJdbcUtils;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.support.R2dbcExceptionSubclassTranslator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;

public class EventuateSpringReactiveJdbcStatementExecutor {
  private R2dbcExceptionSubclassTranslator r2dbcExceptionSubclassTranslator = new R2dbcExceptionSubclassTranslator();

  private ConnectionFactory connectionFactory;

  public EventuateSpringReactiveJdbcStatementExecutor(EventuateReactiveConnectionFactorySelector eventuateReactiveConnectionFactorySelector) {
    connectionFactory = eventuateReactiveConnectionFactorySelector.select();
  }

  public Mono<Integer> update(String sql, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient client = createClient();

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(client.execute(sql), params);

    return genericExecuteSpec.fetch().rowsUpdated();
  }

  public Flux<Map<String, Object>> query(String sql, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient client = DatabaseClient.create(connectionFactory);

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(client.execute(sql), params);

    return genericExecuteSpec.fetch().all();
  }

  public <T> Flux<T> queryForList(String sql, EventuateReactiveRowMapper<T> eventuateReactiveRowMapper, Object... params) {
    sql = reformatParameters(sql, params);

    DatabaseClient client = createClient();

    DatabaseClient.GenericExecuteSpec genericExecuteSpec = bindParameters(client.execute(sql), params);

    return genericExecuteSpec.map(eventuateReactiveRowMapper).all();
  }

  private DatabaseClient createClient() {
    return DatabaseClient.builder()
            .connectionFactory(connectionFactory)
            .exceptionTranslator((task, query, e) -> {
              if (EventuateJdbcUtils.isDuplicateKeyException(e.getSqlState(), e.getErrorCode())) {
                return new DuplicateKeyException(e.getMessage());
              }

              return r2dbcExceptionSubclassTranslator.translate(task, query, e);
            })
            .build();
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
}
