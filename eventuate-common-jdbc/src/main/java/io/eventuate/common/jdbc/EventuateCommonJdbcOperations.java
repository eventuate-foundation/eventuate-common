package io.eventuate.common.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations extends EventuateAbstractJdbcOperations {

  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;

  public EventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                       EventuateSqlDialect eventuateSqlDialect) {
    super(eventuateSqlDialect);

    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
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
              .insertAndReturnGeneratedId(insertIntoEventsTableDbIdSql(eventuateSchema),
                      EVENT_AUTO_GENERATED_ID_COLUMN,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      booleanToInt(published));

      return idGenerator.genId(databaseId).asString();
    }
    else {
      String eventId = idGenerator.genId(null).asString();

      eventuateJdbcStatementExecutor
              .update(insertIntoEventsTableApplicationIdSql(eventuateSchema),
                      eventId,
                      eventType,
                      eventData,
                      entityType,
                      entityId,
                      triggeringEvent.orElse(null),
                      metadata.orElse(null),
                      booleanToInt(published));

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

    if (idGenerator.databaseIdRequired()) {
      return insertIntoMessageTableDatabaseId(idGenerator, payload, destination, headers, published, eventuateSchema);
    }
    else {
      return insertIntoMessageTableApplicationId(idGenerator, payload, destination, headers, published, eventuateSchema);
    }
  }

  private String insertIntoMessageTableApplicationId(IdGenerator idGenerator,
                                                     String payload,
                                                     String destination,
                                                     Map<String, String> headers,
                                                     boolean published,
                                                     EventuateSchema eventuateSchema) {

    headers = new HashMap<>(headers);

    String messageId = idGenerator.genId(null).asString();

    headers.put("ID", messageId);

    String serializedHeaders = JSonMapper.toJson(headers);

    eventuateJdbcStatementExecutor.update(insertIntoMessageTableApplicationIdSql(eventuateSchema),
            messageId, destination, serializedHeaders, payload, booleanToInt(published));

    return messageId;
  }

  private String insertIntoMessageTableDatabaseId(IdGenerator idGenerator,
                                                  String payload,
                                                  String destination,
                                                  Map<String, String> headers,
                                                  boolean published,
                                                  EventuateSchema eventuateSchema) {

    String serializedHeaders = JSonMapper.toJson(headers);

    long databaseId = eventuateJdbcStatementExecutor.insertAndReturnGeneratedId(insertIntoMessageTableDbIdSql(eventuateSchema),
            MESSAGE_AUTO_GENERATED_ID_COLUMN, destination, serializedHeaders, payload, booleanToInt(published));

    return idGenerator.genId(databaseId).asString();
  }

  @Override //TODO: solve issue (see parent method) with postgres and move to parent class
  protected String columnToJson(EventuateSchema eventuateSchema, String column) {
    return getEventuateSqlDialect().castToJson("?",
            eventuateSchema, "message", column, eventuateJdbcStatementExecutor);
  }
}
