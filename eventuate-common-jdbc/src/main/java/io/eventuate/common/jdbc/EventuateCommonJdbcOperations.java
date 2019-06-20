package io.eventuate.common.jdbc;

import io.eventuate.common.json.mapper.JSonMapper;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {

  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;

  public EventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
  }

  public void insertIntoEventsTable(String eventId,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema) throws SQLException {

    String table = eventuateSchema.qualifyTable("events");
    String sql = String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata) VALUES (?, ?, ?, ?, ?, ?, ?);", table);

    eventuateJdbcStatementExecutor.update(sql, eventId, eventType, eventData, entityType, entityId, triggeringEvent.orElse(null), metadata.orElse(null));
  }


  public void insertIntoMessageTable(String messageId,
                                      String payload,
                                      String destination,
                                      String currentTimeInMillisecondsSql,
                                      Map<String, String> headers,
                                      EventuateSchema eventuateSchema) throws SQLException {

    String table = eventuateSchema.qualifyTable("message");
    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time) values(?, ?, ?, ?, %s)", table, currentTimeInMillisecondsSql);
    String serializedHeaders = JSonMapper.toJson(headers);

    eventuateJdbcStatementExecutor.update(sql, messageId, destination, serializedHeaders, payload);
  }
}
