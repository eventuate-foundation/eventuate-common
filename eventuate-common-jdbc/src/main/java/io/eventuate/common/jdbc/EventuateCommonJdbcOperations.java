package io.eventuate.common.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;

public class EventuateCommonJdbcOperations {

  private EventuateJdbcOperationsUtils eventuateJdbcOperationsUtils;
  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;
  private OutboxPartitioningSpec outboxPartitioningSpec;
  private EventuateSqlDialect eventuateSqlDialect;
  public EventuateCommonJdbcOperations(EventuateJdbcOperationsUtils eventuateJdbcOperationsUtils,
                                       EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                       EventuateSqlDialect eventuateSqlDialect,
                                       OutboxPartitioningSpec outboxPartitioningSpec) {
    this.eventuateJdbcOperationsUtils = eventuateJdbcOperationsUtils;
    this.eventuateSqlDialect = eventuateSqlDialect;
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
    this.outboxPartitioningSpec = outboxPartitioningSpec;
  }

  public EventuateSqlDialect getEventuateSqlDialect() {
    return eventuateSqlDialect;
  }

  public String insertIntoEventsTable(IdGenerator idGenerator,
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

  public String insertPublishedEventIntoEventsTable(IdGenerator idGenerator,
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

  private String insertIntoEventsTable(IdGenerator idGenerator,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema,
                                    boolean published) {

    if (idGenerator.databaseIdRequired()) {
      Long databaseId = eventuateJdbcStatementExecutor
              .insertAndReturnGeneratedId(eventuateJdbcOperationsUtils.insertIntoEventsTableDbIdSql(eventuateSchema),
                      EVENT_AUTO_GENERATED_ID_COLUMN,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      eventuateJdbcOperationsUtils.booleanToInt(published));

      return idGenerator.genId(databaseId, null).asString();
    }
    else {
      String eventId = idGenerator.genId().asString();

      eventuateJdbcStatementExecutor
              .update(eventuateJdbcOperationsUtils.insertIntoEventsTableApplicationIdSql(eventuateSchema),
                      eventId,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      eventuateJdbcOperationsUtils.booleanToInt(published));

      return eventId;
    }
  }

  public String insertIntoMessageTable(IdGenerator idGenerator,
                                       String payload,
                                       String destination,
                                       Map<String, String> headers,
                                       EventuateSchema eventuateSchema) {

    return insertIntoMessageTable(idGenerator, payload, destination, headers, eventuateSchema, false);
  }

  public String insertPublishedMessageIntoMessageTable(IdGenerator idGenerator,
                                       String payload,
                                       String destination,
                                       Map<String, String> headers,
                                       EventuateSchema eventuateSchema) {

    return insertIntoMessageTable(idGenerator, payload, destination, headers, eventuateSchema, true);
  }

  private String insertIntoMessageTable(IdGenerator idGenerator,
                                       String payload,
                                       String destination,
                                       Map<String, String> headers,
                                       EventuateSchema eventuateSchema,
                                       boolean published) {

    String messageKey = headers.get("PARTITION_ID");
    OutboxPartitionValues outboxPartitionValues = outboxPartitioningSpec.outboxTableValues(destination, messageKey);

    if (idGenerator.databaseIdRequired()) {
      return insertIntoMessageTableDatabaseId(idGenerator, payload, destination, headers, published, eventuateSchema, outboxPartitionValues);
    }
    else {
      return insertIntoMessageTableApplicationId(idGenerator, payload, destination, headers, published, eventuateSchema, outboxPartitionValues);
    }
  }

  private String insertIntoMessageTableApplicationId(IdGenerator idGenerator,
                                                     String payload,
                                                     String destination,
                                                     Map<String, String> headers,
                                                     boolean published,
                                                     EventuateSchema eventuateSchema, OutboxPartitionValues outboxPartitionValues) {

    headers = new HashMap<>(headers);

    String messageId = idGenerator.genId(null, outboxPartitionValues.outboxTableSuffix.suffix).asString();

    verifyNoID(headers);

    headers.put("ID", messageId);

    String serializedHeaders = JSonMapper.toJson(headers);

    eventuateJdbcStatementExecutor.update(eventuateJdbcOperationsUtils.insertIntoMessageTableApplicationIdSql(eventuateSchema, this::columnToJson, outboxPartitionValues.outboxTableSuffix.suffixAsString),
            messageId, destination, serializedHeaders, payload, eventuateJdbcOperationsUtils.booleanToInt(published), outboxPartitionValues.messagePartition);

    return messageId;
  }

  private void verifyNoID(Map<String, String> headers) {
    if (headers.containsKey("ID"))
      throw new RuntimeException("ID should not be already set");
  }

  private String insertIntoMessageTableDatabaseId(IdGenerator idGenerator,
                                                  String payload,
                                                  String destination,
                                                  Map<String, String> headers,
                                                  boolean published,
                                                  EventuateSchema eventuateSchema, OutboxPartitionValues outboxPartitionValues) {

    verifyNoID(headers);

    String serializedHeaders = JSonMapper.toJson(headers);

    long databaseId = eventuateJdbcStatementExecutor.insertAndReturnGeneratedId(eventuateJdbcOperationsUtils.insertIntoMessageTableDbIdSql(eventuateSchema, this::columnToJson, outboxPartitionValues.outboxTableSuffix.suffixAsString),
            MESSAGE_AUTO_GENERATED_ID_COLUMN, destination, serializedHeaders, payload, eventuateJdbcOperationsUtils.booleanToInt(published), outboxPartitionValues.messagePartition);

    return idGenerator.genId(databaseId, outboxPartitionValues.outboxTableSuffix.suffix).asString();
  }

  protected String columnToJson(EventuateSchema eventuateSchema, String column) {
    return getEventuateSqlDialect().castToJson("?",
            eventuateSchema, "message", column, (sql, args) -> eventuateJdbcStatementExecutor.queryForList(sql, args.toArray()));
  }
}
