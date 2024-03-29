package io.eventuate.common.reactive.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateJdbcOperationsUtils;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.OutboxPartitionValues;
import io.eventuate.common.jdbc.OutboxPartitioningSpec;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;

public class EventuateCommonReactiveJdbcOperations {


  private static final OutboxPartitioningSpec TODO_OUTBOX_PARTITION_SPEC = OutboxPartitioningSpec.DEFAULT; // TODO

  private EventuateJdbcOperationsUtils eventuateJdbcOperationsUtils;
  private EventuateReactiveJdbcStatementExecutor reactiveJdbcStatementExecutor;
  private EventuateSqlDialect eventuateSqlDialect;
  private int blockingTimeoutForRetrievingMetadata;

  public EventuateCommonReactiveJdbcOperations(EventuateJdbcOperationsUtils eventuateJdbcOperationsUtils,
                                               EventuateReactiveJdbcStatementExecutor reactiveJdbcStatementExecutor,
                                               EventuateSqlDialect eventuateSqlDialect,
                                               int blockingTimeoutForRetrievingMetadata) {
    this.eventuateJdbcOperationsUtils = eventuateJdbcOperationsUtils;
    this.reactiveJdbcStatementExecutor = reactiveJdbcStatementExecutor;
    this.eventuateSqlDialect = eventuateSqlDialect;
    this.blockingTimeoutForRetrievingMetadata = blockingTimeoutForRetrievingMetadata;
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
      return reactiveJdbcStatementExecutor
              .insertAndReturnId(eventuateJdbcOperationsUtils.insertIntoEventsTableDbIdSql(eventuateSchema),
                      EVENT_AUTO_GENERATED_ID_COLUMN,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      eventuateJdbcOperationsUtils.booleanToInt(published))
              .map(id -> idGenerator.genId(id, null).asString());
    }
    else {
      String eventId = idGenerator.genIdAsString();

      return reactiveJdbcStatementExecutor
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

    String messageKey = headers.get("PARTITION_ID");
    OutboxPartitionValues outboxPartitionValues = TODO_OUTBOX_PARTITION_SPEC.outboxTableValues(destination, messageKey);

    if (idGenerator.databaseIdRequired()) {
      return insertIntoMessageTableDatabaseId(idGenerator,
              payload, destination, headers, published, eventuateSchema, outboxPartitionValues);
    }
    else {
      return insertIntoMessageTableApplicationId(idGenerator,
              payload, destination, headers, published, eventuateSchema, outboxPartitionValues);
    }
  }

  private void verifyNoID(Map<String, String> headers) {
    if (headers.containsKey("ID"))
      throw new RuntimeException("ID should not be already set");
  }

  private Mono<String> insertIntoMessageTableApplicationId(IdGenerator idGenerator,
                                                           String payload,
                                                           String destination,
                                                           Map<String, String> headers,
                                                           boolean published,
                                                           EventuateSchema eventuateSchema, OutboxPartitionValues outboxPartitionValues) {

    verifyNoID(headers);

    headers = new HashMap<>(headers);

    String messageId = idGenerator.genIdAsString();

    headers.put("ID", messageId);

    String serializedHeaders = JSonMapper.toJson(headers);

    return reactiveJdbcStatementExecutor
            .update(eventuateJdbcOperationsUtils.insertIntoMessageTableApplicationIdSql(eventuateSchema, this::columnToJson, outboxPartitionValues.outboxTableSuffix.suffixAsString),
                    messageId, destination, serializedHeaders, payload, eventuateJdbcOperationsUtils.booleanToInt(published), outboxPartitionValues.messagePartition)
            .map(rowsUpdated -> messageId);
  }

  private Mono<String> insertIntoMessageTableDatabaseId(IdGenerator idGenerator,
                                                        String payload,
                                                        String destination,
                                                        Map<String, String> headers,
                                                        boolean published,
                                                        EventuateSchema eventuateSchema, OutboxPartitionValues outboxPartitionValues) {
    verifyNoID(headers);

    String serializedHeaders = JSonMapper.toJson(headers);

    return reactiveJdbcStatementExecutor
            .insertAndReturnId(eventuateJdbcOperationsUtils.insertIntoMessageTableDbIdSql(eventuateSchema, this::columnToJson, outboxPartitionValues.outboxTableSuffix.suffixAsString),
                    MESSAGE_AUTO_GENERATED_ID_COLUMN, destination, serializedHeaders, payload, eventuateJdbcOperationsUtils.booleanToInt(published), outboxPartitionValues.messagePartition)
            .map(id -> idGenerator.genIdAsString(id, outboxPartitionValues.outboxTableSuffix.suffix));
  }

  public String columnToJson(EventuateSchema eventuateSchema, String column) {

    BiFunction<String, List<Object>, List<Map<String, Object>>> selectCallback =
            (sql, params) -> reactiveJdbcStatementExecutor
                    .query(sql, params.toArray())
                    .collectList()
                    .block(Duration.ofMillis(blockingTimeoutForRetrievingMetadata));

    return eventuateSqlDialect.castToJson("?",
            eventuateSchema, "message", column, selectCallback);
  }
}
