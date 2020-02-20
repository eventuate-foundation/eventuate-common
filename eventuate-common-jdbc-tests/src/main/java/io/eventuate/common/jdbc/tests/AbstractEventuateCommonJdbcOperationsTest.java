package io.eventuate.common.jdbc.tests;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.json.mapper.JSonMapper;
import org.junit.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public abstract class AbstractEventuateCommonJdbcOperationsTest {
  private EventuateSchema eventuateSchema = new EventuateSchema();

  protected abstract EventuateCommonJdbcOperations getEventuateCommonJdbcOperations();
  protected abstract EventuateTransactionTemplate getEventuateTransactionTemplate();
  protected abstract DataSource getDataSource();

  public void testEventuateDuplicateKeyException() {
    String eventId = generateId();
    String entityId = generateId();
    String eventData = generateId();
    String eventType = generateId();
    String entityType = generateId();
    String triggeringEvent = generateId();
    String metadata = generateId();

    getEventuateTransactionTemplate().executeInTransaction(() -> {

      getEventuateCommonJdbcOperations().insertIntoEventsTable(eventId,
              entityId, eventData, eventType, entityType, Optional.of(triggeringEvent), Optional.of(metadata), eventuateSchema);

      getEventuateCommonJdbcOperations().insertIntoEventsTable(eventId,
              entityId, eventData, eventType, entityType, Optional.of(triggeringEvent), Optional.of(metadata), eventuateSchema);

      return null;
    });
  }

  public void testInsertIntoEventsTable() throws SQLException {
    String eventId = generateId();
    String entityId = generateId();
    String eventData = generateId();
    String eventType = generateId();
    String entityType = generateId();
    String triggeringEvent = generateId();
    String metadata = generateId();

    getEventuateTransactionTemplate().executeInTransaction(() -> {
      getEventuateCommonJdbcOperations().insertIntoEventsTable(eventId,
              entityId, eventData, eventType, entityType, Optional.of(triggeringEvent), Optional.of(metadata), eventuateSchema);

      return null;
    });

    List<Map<String, Object>> events = getEvents(eventId);

    Assert.assertEquals(1, events.size());

    Map<String, Object> event = events.get(0);

    Assert.assertEquals(eventType, event.get("event_type"));
    Assert.assertEquals(eventData, event.get("event_data"));
    Assert.assertEquals(entityType, event.get("entity_type"));
    Assert.assertEquals(entityId, event.get("entity_id"));
    Assert.assertEquals(triggeringEvent, event.get("triggering_event"));
    Assert.assertEquals(metadata, event.get("metadata"));
  }

  public void testInsertIntoMessageTable() throws SQLException {
    String messageId = generateId();
    String payload = generateId();
    String destination = generateId();
    Long time = System.nanoTime();
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("header1k", "header1v");
    headers.put("header2k", "header2v");

    getEventuateTransactionTemplate().executeInTransaction(() -> {
      getEventuateCommonJdbcOperations().insertIntoMessageTable(messageId,
              payload,
              destination,
              time.toString(),
              headers,
              eventuateSchema);

      return null;
    });

    List<Map<String, Object>> messages = getMessages(messageId);

    Assert.assertEquals(1, messages.size());

    Map<String, Object> event = messages.get(0);

    Assert.assertEquals(destination, event.get("destination"));
    Assert.assertEquals(payload, event.get("payload"));
    Assert.assertEquals(time, event.get("creation_time"));
    Assert.assertEquals(JSonMapper.toJson(headers), event.get("headers"));
  }

  private String generateId() {
    return UUID.randomUUID().toString();
  }

  private List<Map<String, Object>> getEvents(String eventId) {
    String table = eventuateSchema.qualifyTable("events");
    String sql = String.format("select event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata from %s where event_id = ?", table);

    try (Connection connection = getDataSource().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


      preparedStatement.setString(1, eventId);

      List<Map<String, Object>> events = new ArrayList<>();

      try(ResultSet rs = preparedStatement.executeQuery()) {
        while (rs.next()) {
          Map<String, Object> event = new HashMap<>();

          event.put("event_id", rs.getString("event_id"));
          event.put("event_type", rs.getString("event_type"));
          event.put("event_data", rs.getString("event_data"));
          event.put("entity_type", rs.getString("entity_type"));
          event.put("entity_id", rs.getString("entity_id"));
          event.put("triggering_event", rs.getString("triggering_event"));
          event.put("metadata", rs.getString("metadata"));

          events.add(event);
        }
      }

      return events;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private List<Map<String, Object>> getMessages(String messageId) {
    String table = eventuateSchema.qualifyTable("message");
    String sql = String.format("select id, destination, headers, payload, creation_time from %s where id = ?", table);

    try (Connection connection = getDataSource().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


      preparedStatement.setString(1, messageId);

      List<Map<String, Object>> messages = new ArrayList<>();

      try(ResultSet rs = preparedStatement.executeQuery()) {
        while (rs.next()) {
          Map<String, Object> message = new HashMap<>();

          message.put("id", rs.getString("id"));
          message.put("destination", rs.getString("destination"));
          message.put("headers", rs.getString("headers"));
          message.put("payload", rs.getString("payload"));
          message.put("creation_time", rs.getLong("creation_time"));

          messages.add(message);
        }
      }

      return messages;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
