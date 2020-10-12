package io.eventuate.common.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {

  public static final String MESSAGE_AUTO_GENERATED_ID_COLUMN = "dbid";
  public static final String EVENT_AUTO_GENERATED_ID_COLUMN = "id";

  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;
  private EventuateSqlDialect eventuateSqlDialect;

  public EventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                       EventuateSqlDialect eventuateSqlDialect) {
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
    this.eventuateSqlDialect = eventuateSqlDialect;
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

  public String insertIntoEventsTable(IdGenerator idGenerator,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema,
                                    boolean published) {

    String table = eventuateSchema.qualifyTable("events");

    if (idGenerator.databaseIdRequired()) {
      String sql = String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)" +
              " VALUES ('', ?, ?, ?, ?, ?, ?, ?);", table);

      Long databaseId = eventuateJdbcStatementExecutor
              .insertAndReturnGeneratedId(sql,
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
      String sql = String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)" +
              " VALUES (?, ?, ?, ?, ?, ?, ?, ?);", table);

      String eventId = idGenerator.genId(null).asString();

      eventuateJdbcStatementExecutor
              .update(sql,
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

    String table = eventuateSchema.qualifyTable("message");

    String jsonHeadersColumn = columnToJson(eventuateSchema, "headers");
    String jsonPayloadColumn = columnToJson(eventuateSchema, "payload");

    if (idGenerator.databaseIdRequired()) {
      return insertIntoMessageTableDatabaseId(idGenerator,
              table, jsonHeadersColumn, jsonPayloadColumn, payload, destination, headers, published);
    }
    else
    {
      return insertIntoMessageTableApplicationId(idGenerator,
              table, jsonHeadersColumn, jsonPayloadColumn, payload, destination, headers, published);
    }
  }

  private String insertIntoMessageTableApplicationId(IdGenerator idGenerator,
                                                     String table,
                                                     String jsonHeadersColumn,
                                                     String jsonPayloadColumn,
                                                     String payload,
                                                     String destination,
                                                     Map<String, String> headers,
                                                     boolean published) {

    headers = new HashMap<>(headers);

    String messageId = idGenerator.genId(null).asString();

    headers.put("ID", messageId);

    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time, published) values(?, ?, %s, %s, %s, ?)",
            table,
            jsonHeadersColumn,
            jsonPayloadColumn,
            eventuateSqlDialect.getCurrentTimeInMillisecondsExpression());

    String serializedHeaders = JSonMapper.toJson(headers);

    eventuateJdbcStatementExecutor.update(sql, messageId, destination, serializedHeaders, payload, booleanToInt(published));

    return messageId;
  }

  private String insertIntoMessageTableDatabaseId(IdGenerator idGenerator,
                                                  String table,
                                                  String jsonHeadersColumn,
                                                  String jsonPayloadColumn,
                                                  String payload,
                                                  String destination,
                                                  Map<String, String> headers,
                                                  boolean published) {

    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time, published) values('', ?, %s, %s, %s, ?)",
            table,
            jsonHeadersColumn,
            jsonPayloadColumn,
            eventuateSqlDialect.getCurrentTimeInMillisecondsExpression());

    String serializedHeaders = JSonMapper.toJson(headers);

    long databaseId = eventuateJdbcStatementExecutor.insertAndReturnGeneratedId(sql,
            MESSAGE_AUTO_GENERATED_ID_COLUMN, destination, serializedHeaders, payload, booleanToInt(published));

    return idGenerator.genId(databaseId).asString();
  }

  private int booleanToInt(boolean bool) {
    return bool ? 1 : 0;
  }

  private String columnToJson(EventuateSchema eventuateSchema, String column) {
    return eventuateSqlDialect.castToJson("?",
            eventuateSchema, "message", column, eventuateJdbcStatementExecutor);
  }
}
