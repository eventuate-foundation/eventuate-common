package io.eventuate.common.jdbc.tests;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.Int128;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.MySqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateJdbcOperationsUtils.MESSAGE_AUTO_GENERATED_ID_COLUMN;


public abstract class AbstractEventuateCommonJdbcOperationsTest {
  protected EventuateSchema eventuateSchema = new EventuateSchema();

  protected abstract EventuateTransactionTemplate getEventuateTransactionTemplate();
  protected abstract IdGenerator getIdGenerator();
  protected abstract DataSource getDataSource();
  protected abstract EventuateSqlDialect getEventuateSqlDialect();

  protected abstract String insertIntoMessageTable(String payload,
                                                   String destination,
                                                   Map<String, String> headers);

  protected abstract String insertIntoEventsTable(String entityId,
                                                      String eventData,
                                                      String eventType,
                                                      String entityType,
                                                      Optional<String> triggeringEvent,
                                                      Optional<String> metadata);

  protected abstract void insertIntoEntitiesTable(String entityId, String entityType, EventuateSchema eventuateSchema);

  public void testEventuateDuplicateKeyException() {
    String entityId = generateId();
    String entityType = generateId();

    insertIntoEntitiesTable(entityId, entityType, eventuateSchema);
    insertIntoEntitiesTable(entityId, entityType, eventuateSchema);
  }

  public void testInsertIntoEventsTable() throws SQLException {
    String entityId = generateId();
    String eventData = generateId();
    String eventType = generateId();
    String entityType = generateId();
    String triggeringEvent = generateId();
    String metadata = generateId();

    String eventId = insertIntoEventsTable(entityId,
              eventData,
              eventType,
              entityType,
              Optional.of(triggeringEvent),
              Optional.of(metadata));

    List<Map<String, Object>> events = getEvents(eventIdToRowId(eventId));

    Assertions.assertEquals(1, events.size());

    Map<String, Object> event = events.get(0);

    boolean eventIdIsEmpty = StringUtils.isEmpty((String) event.get("event_id"));
    if (getIdGenerator().databaseIdRequired()) {
      Assertions.assertTrue(eventIdIsEmpty);
    } else {
      Assertions.assertFalse(eventIdIsEmpty);
    }

    Assertions.assertEquals(eventType, event.get("event_type"));
    Assertions.assertEquals(eventData, event.get("event_data"));
    Assertions.assertEquals(entityType, event.get("entity_type"));
    Assertions.assertEquals(entityId, event.get("entity_id"));
    Assertions.assertEquals(triggeringEvent, event.get("triggering_event"));
    Assertions.assertEquals(metadata, event.get("metadata"));
  }

  public void testInsertIntoMessageTable() throws SQLException {
    String payload = "\"" + generateId() + "\"";
    String destination = generateId();
    Map<String, String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("header1k", "header1v");
    expectedHeaders.put("header2k", "header2v");

    String messageId = insertIntoMessageTable(payload, destination, expectedHeaders);

    List<Map<String, Object>> messages = getMessages(messageIdToRowId(messageId));

    Assertions.assertEquals(1, messages.size());

    Map<String, Object> event = messages.get(0);

    Map<String, String> actualHeaders = JSonMapper.fromJson(event.get("headers").toString(), Map.class);

    if (!getIdGenerator().databaseIdRequired()) {
      Assertions.assertTrue(actualHeaders.containsKey("ID"));
      Assertions.assertEquals(messageId, actualHeaders.get("ID"));
      actualHeaders.remove("ID");
    }

    Assertions.assertEquals(destination, event.get("destination"));
    Assertions.assertEquals(payload, event.get("payload"));
    //since time is generated automatically now, it is hard to predict accurate time. So there is estimated time is used (5 min accuracy)
    Assertions.assertTrue(System.currentTimeMillis() - (long) event.get("creation_time") < 5 * 60 * 1000);
    Assertions.assertEquals(expectedHeaders, actualHeaders);
  }

  protected void testGeneratedIdOfEventsTableRow() {
    testGeneratedId(this::insertRandomEvent, this::assertIdAnchorEventCreated);
  }

  private long insertRandomEvent() {
    return (long)eventIdToRowId(insertIntoEventsTable(generateId(),
            generateId(),
            generateId(),
            generateId(),
            Optional.of(generateId()),
            Optional.of(generateId())))
            .getValue();
  }

  private void assertIdAnchorEventCreated() {
    if (!(getEventuateSqlDialect() instanceof MySqlDialect)) {
      return;
    }

    getEventuateTransactionTemplate().executeInTransaction(() -> {
      String table = eventuateSchema.qualifyTable("events");
      String sql = "select * from %s where event_type = 'CDC-IGNORED'".formatted(table);
      try (Connection connection = getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        try(ResultSet rs = preparedStatement.executeQuery()) {
          Assertions.assertTrue(rs.next());
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      return null;
    });
  }

  protected void testGeneratedIdOfMessageTableRow() {
    testGeneratedId(this::insertRandomMessage, this::assertIdAnchorMessageCreated);
  }

  private long insertRandomMessage() {
    return (long) messageIdToRowId(insertIntoMessageTable("\"" + generateId() + "\"",
            generateId(),
            Collections.emptyMap()))
            .getValue();
  }

  private void assertIdAnchorMessageCreated() {
    if (!(getEventuateSqlDialect() instanceof MySqlDialect)) {
      return;
    }

    getEventuateTransactionTemplate().executeInTransaction(() -> {
      String table = eventuateSchema.qualifyTable("message");
      String sql = "select * from %s where destination = 'CDC-IGNORED'".formatted(table);
      try (Connection connection = getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        try(ResultSet rs = preparedStatement.executeQuery()) {
          Assertions.assertTrue(rs.next());
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      return null;
    });
  }

  private void testGeneratedId(Supplier<Long> insertOperation, Runnable idAnchorVerificationCallback) {
    if (!getIdGenerator().databaseIdRequired()) return; //nothing to do

    idAnchorVerificationCallback.run();

    long rowId = insertOperation.get();

    assertIdSequenceUsesCurrentTimeAsStartingValue(rowId);
  }

  protected String generateId() {
    return UUID.randomUUID().toString();
  }

  protected List<Map<String, Object>> getEvents(IdColumnAndValue idColumnAndValue) {
    return getEventuateTransactionTemplate().executeInTransaction(() -> {
      String table = eventuateSchema.qualifyTable("events");
      String sql = "select event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata from %s where %s = ?".formatted(
              table, idColumnAndValue.getColumn());

      try (Connection connection = getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


        preparedStatement.setObject(1, idColumnAndValue.getValue());

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
    });
  }

  protected List<Map<String, Object>> getMessages(IdColumnAndValue idColumnAndValue) {
    return getEventuateTransactionTemplate().executeInTransaction(() -> {
      String table = eventuateSchema.qualifyTable("message");
      String sql = "select %s, destination, headers, payload, creation_time from %s where %s = ?".formatted(
              idColumnAndValue.getColumn(), table, idColumnAndValue.getColumn());

      try (Connection connection = getDataSource().getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


        preparedStatement.setObject(1, idColumnAndValue.getValue());

        List<Map<String, Object>> messages = new ArrayList<>();

        try(ResultSet rs = preparedStatement.executeQuery()) {
          while (rs.next()) {
            Map<String, Object> message = new HashMap<>();

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
    });
  }

  protected IdColumnAndValue messageIdToRowId(String messageId) {
    if (getIdGenerator().databaseIdRequired()) {
      return new IdColumnAndValue(MESSAGE_AUTO_GENERATED_ID_COLUMN, extractRowIdFromEventId(messageId));
    }

    return new IdColumnAndValue("id", messageId);
  }

  protected IdColumnAndValue eventIdToRowId(String eventId) {
    if (getIdGenerator().databaseIdRequired()) {
      return new IdColumnAndValue(EVENT_AUTO_GENERATED_ID_COLUMN, extractRowIdFromEventId(eventId));
    }

    return new IdColumnAndValue("event_id", eventId);
  }

  private long extractRowIdFromEventId(String id) {
    return Int128.fromString(id).getHi();
  }

  //(database id generation) The auto generated values must greater than any existing message IDs
  //https://github.com/eventuate-foundation/eventuate-common/issues/53
  private void assertIdSequenceUsesCurrentTimeAsStartingValue(long id) {
    final long precision = TimeUnit.HOURS.toMillis(1);

    long currentTime = System.currentTimeMillis();

    Assertions.assertTrue(currentTime - id < precision,
            "Row id should start from current time in milliseconds after migration (current time: %s, id: %s)".formatted(currentTime, id));
  }

  protected static class IdColumnAndValue {
    private String column;
    private Object value;

    public IdColumnAndValue(String column, Object value) {
      this.column = column;
      this.value = value;
    }

    public String getColumn() {
      return column;
    }

    public Object getValue() {
      return value;
    }
  }
}
