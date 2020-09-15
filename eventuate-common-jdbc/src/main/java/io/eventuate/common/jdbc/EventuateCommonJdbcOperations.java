package io.eventuate.common.jdbc;

import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;

import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {

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


  public Long insertIntoMessageTable(String payload,
                                      String destination,
                                      String currentTimeInMillisecondsSql,
                                      Map<String, String> headers,
                                      EventuateSchema eventuateSchema) {

    String table = eventuateSchema.qualifyTable("message");

    String sql = String.format("insert into %s(destination, headers, payload, creation_time) values(?, %s, %s, %s)",
            table,
            eventuateSqlDialect.castToJson("?", eventuateSchema, "message", "headers", eventuateJdbcStatementExecutor),
            eventuateSqlDialect.castToJson("?", eventuateSchema, "message","payload", eventuateJdbcStatementExecutor),
            currentTimeInMillisecondsSql);

    String serializedHeaders = JSonMapper.toJson(headers);

    return eventuateJdbcStatementExecutor.insertAndReturnGeneratedId(sql, destination, serializedHeaders, payload);
  }
}
