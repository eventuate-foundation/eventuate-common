package io.eventuate.common.spring.jdbc.reactive;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateJdbcOperationsUtils;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;

public class EventuateCommonReactiveSpringJdbcOperations {

  private EventuateJdbcOperationsUtils eventuateJdbcOperationsUtils;
  private EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor;
  private EventuateSqlDialect eventuateSqlDialect;

  public EventuateCommonReactiveSpringJdbcOperations(EventuateJdbcOperationsUtils eventuateJdbcOperationsUtils,
                                                     EventuateSpringReactiveJdbcStatementExecutor eventuateSpringReactiveJdbcStatementExecutor,
                                                     EventuateSqlDialect eventuateSqlDialect) {
    this.eventuateJdbcOperationsUtils = eventuateJdbcOperationsUtils;
    this.eventuateSpringReactiveJdbcStatementExecutor = eventuateSpringReactiveJdbcStatementExecutor;
    this.eventuateSqlDialect = eventuateSqlDialect;
  }

  public EventuateSqlDialect getEventuateSqlDialect() {
    return eventuateSqlDialect;
  }

  public Mono<String> insertIntoEventsTable(IdGenerator idGenerator,
                                      String entityId,
                                      String eventData,
                                      String eventType,
                                      String entityType,
                                      Optional<String> triggeringEvent,
                                      Optional<String> metadata,
                                      EventuateSchema eventuateSchema) {

    return insertIntoEventsTable(idGenerator,
            entityId, eventData, eventType, entityType, triggeringEvent, metadata, eventuateSchema, false);
  }

  public Mono<String> insertPublishedEventIntoEventsTable(IdGenerator idGenerator,
                                      String entityId,
                                      String eventData,
                                      String eventType,
                                      String entityType,
                                      Optional<String> triggeringEvent,
                                      Optional<String> metadata,
                                      EventuateSchema eventuateSchema) {

    return insertIntoEventsTable(idGenerator,
            entityId, eventData, eventType, entityType, triggeringEvent, metadata, eventuateSchema, true);
  }

  private Mono<String> insertIntoEventsTable(IdGenerator idGenerator,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema,
                                    boolean published) {

    if (idGenerator.databaseIdRequired()) {
      return eventuateSpringReactiveJdbcStatementExecutor
              .insertAndReturnId(eventuateJdbcOperationsUtils.insertIntoEventsTableDbIdSql(eventuateSchema),
                      EVENT_AUTO_GENERATED_ID_COLUMN,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      eventuateJdbcOperationsUtils.booleanToInt(published))
              .map(id -> idGenerator.genId(id).asString());
    }
    else {
      String eventId = idGenerator.genId(null).asString();

      return eventuateSpringReactiveJdbcStatementExecutor
              .update(eventuateJdbcOperationsUtils.insertIntoEventsTableApplicationIdSql(eventuateSchema),
                      eventId,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      eventuateJdbcOperationsUtils.booleanToInt(published))
              .map(rows -> eventId);
    }
  }

  public Mono<String> insertIntoMessageTable(IdGenerator idGenerator,
                                       String payload,
                                       String destination,
                                       Map<String, String> headers,
                                       EventuateSchema eventuateSchema) {

    return insertIntoMessageTable(idGenerator, payload, destination, headers, eventuateSchema, false);
  }

  public Mono<String> insertPublishedMessageIntoMessageTable(IdGenerator idGenerator,
                                       String payload,
                                       String destination,
                                       Map<String, String> headers,
                                       EventuateSchema eventuateSchema) {

    return insertIntoMessageTable(idGenerator, payload, destination, headers, eventuateSchema, true);
  }

  private Mono<String> insertIntoMessageTable(IdGenerator idGenerator,
                                              String payload,
                                              String destination,
                                              Map<String, String> headers,
                                              EventuateSchema eventuateSchema,
                                              boolean published) {
    if (idGenerator.databaseIdRequired()) {
      return insertIntoMessageTableDatabaseId(idGenerator,
              payload, destination, headers, published, eventuateSchema);
    }
    else {
      return insertIntoMessageTableApplicationId(idGenerator,
              payload, destination, headers, published, eventuateSchema);
    }
  }

  private Mono<String> insertIntoMessageTableApplicationId(IdGenerator idGenerator,
                                                           String payload,
                                                           String destination,
                                                           Map<String, String> headers,
                                                           boolean published,
                                                           EventuateSchema eventuateSchema) {

    headers = new HashMap<>(headers);

    String messageId = idGenerator.genId(null).asString();

    headers.put("ID", messageId);

    String serializedHeaders = JSonMapper.toJson(headers);

    return eventuateSpringReactiveJdbcStatementExecutor
            .update(eventuateJdbcOperationsUtils.insertIntoMessageTableApplicationIdSql(eventuateSchema),
                    messageId, destination, serializedHeaders, payload, eventuateJdbcOperationsUtils.booleanToInt(published))
            .map(rowsUpdated -> messageId);
  }

  private Mono<String> insertIntoMessageTableDatabaseId(IdGenerator idGenerator,
                                                        String payload,
                                                        String destination,
                                                        Map<String, String> headers,
                                                        boolean published,
                                                        EventuateSchema eventuateSchema) {
    String serializedHeaders = JSonMapper.toJson(headers);

    return eventuateSpringReactiveJdbcStatementExecutor
            .insertAndReturnId(eventuateJdbcOperationsUtils.insertIntoMessageTableDbIdSql(eventuateSchema),
                    MESSAGE_AUTO_GENERATED_ID_COLUMN, destination, serializedHeaders, payload, eventuateJdbcOperationsUtils.booleanToInt(published))
            .map(id -> idGenerator.genId(id).asString());
  }
}
