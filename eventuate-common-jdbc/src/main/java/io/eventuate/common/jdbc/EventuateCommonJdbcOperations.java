package io.eventuate.common.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {

  public static final String MESSAGE_AUTO_GENERATED_ID_COLUMN = "dbid";

  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;
  private EventuateSqlDialect eventuateSqlDialect;

  public EventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                       EventuateSqlDialect eventuateSqlDialect) {
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
    this.eventuateSqlDialect = eventuateSqlDialect;
  }

  public void insertIntoEventsTable(String eventId,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema) {

    String table = eventuateSchema.qualifyTable("events");
    String sql = String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata) VALUES (?, ?, ?, ?, ?, ?, ?);", table);

    eventuateJdbcStatementExecutor.update(sql, eventId, eventType, eventData, entityType, entityId, triggeringEvent.orElse(null), metadata.orElse(null));
  }


  public String insertIntoMessageTable(IdGenerator idGenerator,
                                       String payload,
                                       String destination,
                                       String currentTimeInMillisecondsSql,
                                       Map<String, String> headers,
                                       EventuateSchema eventuateSchema) {

    String table = eventuateSchema.qualifyTable("message");

    String jsonHeadersColumn = columnToJson(eventuateSchema, "headers");
    String jsonPayloadColumn = columnToJson(eventuateSchema, "payload");

    if (idGenerator.databaseIdRequired()) {
      return insertIntoMessageTableDatabaseId(idGenerator,
              table, jsonHeadersColumn, jsonPayloadColumn, currentTimeInMillisecondsSql, payload, destination, headers);
    }
    else
    {
      return insertIntoMessageTableApplicationId(idGenerator,
              table, jsonHeadersColumn, jsonPayloadColumn, currentTimeInMillisecondsSql, payload, destination, headers);
    }
  }

  private String insertIntoMessageTableApplicationId(IdGenerator idGenerator,
                                                     String table,
                                                     String jsonHeadersColumn,
                                                     String jsonPayloadColumn,
                                                     String currentTimeInMillisecondsSql,
                                                     String payload,
                                                     String destination,
                                                     Map<String, String> headers) {

    headers = new HashMap<>(headers);

    String messageId = idGenerator.genId(null).asString();

    headers.put("ID", messageId);

    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time) values(?, ?, %s, %s, %s)",
            table,
            jsonHeadersColumn,
            jsonPayloadColumn,
            currentTimeInMillisecondsSql);

    String serializedHeaders = JSonMapper.toJson(headers);

    eventuateJdbcStatementExecutor.update(sql, messageId, destination, serializedHeaders, payload);

    return messageId;
  }

  private String insertIntoMessageTableDatabaseId(IdGenerator idGenerator,
                                                  String table,
                                                  String jsonHeadersColumn,
                                                  String jsonPayloadColumn,
                                                  String currentTimeInMillisecondsSql,
                                                  String payload,
                                                  String destination,
                                                  Map<String, String> headers) {

    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time) values('', ?, %s, %s, %s)",
            table,
            jsonHeadersColumn,
            jsonPayloadColumn,
            currentTimeInMillisecondsSql);

    String serializedHeaders = JSonMapper.toJson(headers);

    long databaseId = eventuateJdbcStatementExecutor.insertAndReturnGeneratedId(sql,
            MESSAGE_AUTO_GENERATED_ID_COLUMN, destination, serializedHeaders, payload);

    return idGenerator.genId(databaseId).asString();
  }

  private String columnToJson(EventuateSchema eventuateSchema, String column) {
    return eventuateSqlDialect.castToJson("?",
            eventuateSchema, "message", column, eventuateJdbcStatementExecutor);
  }
}
