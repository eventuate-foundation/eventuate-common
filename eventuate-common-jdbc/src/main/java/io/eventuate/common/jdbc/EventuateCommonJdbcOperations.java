package io.eventuate.common.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {
  private JdbcTemplate jdbcTemplate;

  private ObjectMapper objectMapper = new ObjectMapper();

  public EventuateCommonJdbcOperations(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void insertIntoEventsTable(String eventId,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema) {

    jdbcTemplate.update(String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata) VALUES (?, ?, ?, ?, ?, ?, ?)",
            eventuateSchema.qualifyTable("events")),
            eventId,
            eventType,
            eventData,
            entityType,
            entityId,
            triggeringEvent.orElse(null),
            metadata.orElse(null));
  }

  public void insertIntoMessageTable(String messageId,
                                      String payload,
                                      String destination,
                                      String currentTimeInMillisecondsSql,
                                      Map<String, String> headers,
                                      EventuateSchema eventuateSchema) {
    String table = eventuateSchema.qualifyTable("message");

    String serializedHeaders;
    try {
      serializedHeaders = objectMapper.writeValueAsString(headers);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    jdbcTemplate.update(String.format("insert into %s(id, destination, headers, payload, creation_time) values(?, ?, ?, ?, %s)",
            table, currentTimeInMillisecondsSql),
            messageId,
            destination,
            serializedHeaders,
            payload);
  }
}
