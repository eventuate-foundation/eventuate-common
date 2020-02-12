package io.eventuate.common.jdbc.spring.common;

import io.eventuate.common.jdbc.EventuateJdbcUtils;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.SqlWithParameters;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

public class EventuateCommonReactiveJdbcOperations {

  private EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor;

  public EventuateCommonReactiveJdbcOperations(EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor) {
    this.eventuateSpringReactiveJdbcStatementExecutor = eventuateSpringReactiveJdbcStatementExecutor;
  }

  public Mono<Integer> insertIntoEventsTable(String eventId,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema) {

    SqlWithParameters sqlWithParameters = EventuateJdbcUtils.createInsertIntoEventsTableSql(eventId,
            entityId,
            eventData,
            eventType,
            entityType,
            triggeringEvent,
            metadata,
            eventuateSchema);

    return eventuateSpringReactiveJdbcStatementExecutor.update(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
  }


  public Mono<Integer> insertIntoMessageTable(String messageId,
                                      String payload,
                                      String destination,
                                      String currentTimeInMillisecondsSql,
                                      Map<String, String> headers,
                                      EventuateSchema eventuateSchema) {

    SqlWithParameters sqlWithParameters = EventuateJdbcUtils.createInsertIntoMessageTableSql(messageId,
            payload,
            destination,
            currentTimeInMillisecondsSql,
            headers,
            eventuateSchema);


    return eventuateSpringReactiveJdbcStatementExecutor.update(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
  }
}
