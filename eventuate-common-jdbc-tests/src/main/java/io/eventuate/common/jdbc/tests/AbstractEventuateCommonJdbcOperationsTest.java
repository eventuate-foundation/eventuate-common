package io.eventuate.common.jdbc.tests;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.Int128;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import io.eventuate.common.jdbc.sqldialect.MySqlDialect;
import io.eventuate.common.json.mapper.JSonMapper;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.eventuate.common.jdbc.EventuateCommonJdbcOperations.EVENT_AUTO_GENERATED_ID_COLUMN;
import static io.eventuate.common.jdbc.EventuateCommonJdbcOperations.MESSAGE_AUTO_GENERATED_ID_COLUMN;


public abstract class AbstractEventuateCommonJdbcOperationsTest {
  private EventuateSchema eventuateSchema = new EventuateSchema();

  protected abstract EventuateCommonJdbcOperations getEventuateCommonJdbcOperations();
  protected abstract EventuateTransactionTemplate getEventuateTransactionTemplate();
  protected abstract IdGenerator getIdGenerator();
  protected abstract DataSource getDataSource();
  protected abstract EventuateJdbcStatementExecutor getEventuateJdbcStatementExecutor();

  public void testEventuateDuplicateKeyException() {
    String table = eventuateSchema.qualifyTable("entities");

    String sql = String.format("insert into %s values (?, ?, ?);", table);

    String entityId = generateId();
    String entityType = generateId();

    getEventuateTransactionTemplate().executeInTransaction(() -> {

      getEventuateJdbcStatementExecutor().update(sql, entityType, entityId, System.nanoTime());
      getEventuateJdbcStatementExecutor().update(sql, entityType, entityId, System.nanoTime());

      return null;
    });
  }

  public void testInsertIntoEventsTable() throws SQLException {
    String entityId = generateId();
    String eventData = generateId();
    String eventType = generateId();
    String entityType = generateId();
    String triggeringEvent = generateId();
    String metadata = generateId();

    String eventId = getEventuateTransactionTemplate().executeInTransaction(() ->
      getEventuateCommonJdbcOperations().insertIntoEventsTable(getIdGenerator(),
              entityId,
              eventData,
              eventType,
              entityType,
              Optional.of(triggeringEvent),
              Optional.of(metadata),
              eventuateSchema));

    List<Map<String, Object>> events = getEvents(eventIdToRowId(eventId));

    Assert.assertEquals(1, events.size());

    Map<String, Object> event = events.get(0);

    boolean eventIdIsEmpty = StringUtils.isEmpty((String) event.get("event_id"));
    if (getIdGenerator().databaseIdRequired()) {
      Assert.assertTrue(eventIdIsEmpty);
    } else {
      Assert.assertFalse(eventIdIsEmpty);
    }

    Assert.assertEquals(eventType, event.get("event_type"));
    Assert.assertEquals(eventData, event.get("event_data"));
    Assert.assertEquals(entityType, event.get("entity_type"));
    Assert.assertEquals(entityId, event.get("entity_id"));
    Assert.assertEquals(triggeringEvent, event.get("triggering_event"));
    Assert.assertEquals(metadata, event.get("metadata"));
  }

  public void testInsertIntoMessageTable() throws SQLException {
    String payload = "\"" + generateId() + "\"";
    String destination = generateId();
    Map<String, String> expectedHeaders = new HashMap<>();
    expectedHeaders.put("header1k", "header1v");
    expectedHeaders.put("header2k", "header2v");

    String messageId = getEventuateTransactionTemplate().executeInTransaction(() ->
      getEventuateCommonJdbcOperations().insertIntoMessageTable(getIdGenerator(),
              payload,
              destination,
              expectedHeaders,
              eventuateSchema));

    List<Map<String, Object>> messages = getMessages(messageIdToRowId(messageId));

    Assert.assertEquals(1, messages.size());

    Map<String, Object> event = messages.get(0);

    Map<String, String> actualHeaders = JSonMapper.fromJson(event.get("headers").toString(), Map.class);

    if (!getIdGenerator().databaseIdRequired()) {
      Assert.assertTrue(actualHeaders.containsKey("ID"));
      Assert.assertEquals(messageId, actualHeaders.get("ID"));
      actualHeaders.remove("ID");
    }

    Assert.assertEquals(destination, event.get("destination"));
    Assert.assertEquals(payload, event.get("payload"));
    //since time is generated automatically now, it is hard to predict accurate time. So there is estimated time is used (5 min accuracy)
    Assert.assertTrue(System.currentTimeMillis() - (long) event.get("creation_time") < 5 * 60 * 1000);
    Assert.assertEquals(expectedHeaders, actualHeaders);
  }

  protected void testGeneratedIdOfEventsTableRow() {
    testGeneratedId(this::insertRandomEvent, this::assertIdAnchorEventCreated);
  }

  private long insertRandomEvent() {
    return getEventuateTransactionTemplate().executeInTransaction(() ->
            (long)eventIdToRowId(getEventuateCommonJdbcOperations().insertIntoEventsTable(getIdGenerator(),
                    generateId(),
                    generateId(),
                    generateId(),
                    generateId(),
                    Optional.of(generateId()),
                    Optional.of(generateId()),
                    eventuateSchema)).getValue());
  }

  private void assertIdAnchorEventCreated() {
    if (getEventuateCommonJdbcOperations().getEventuateSqlDialect() instanceof MySqlDialect) {
      List<Map<String, Object>> anchorEvents =
              getEventuateJdbcStatementExecutor().queryForList("select * from eventuate.events where event_type = 'CDC-IGNORED'");

      Assert.assertEquals(1, anchorEvents.size());
    }
  }

  protected void testGeneratedIdOfMessageTableRow() {
    testGeneratedId(this::insertRandomMessage, this::assertIdAnchorMessageCreated);
  }

  private long insertRandomMessage() {
    return getEventuateTransactionTemplate().executeInTransaction(() ->
            (long)messageIdToRowId(getEventuateCommonJdbcOperations().insertIntoMessageTable(getIdGenerator(),
                    "\"" + generateId() + "\"",
                    generateId(),
                    Collections.emptyMap(),
                    eventuateSchema)).getValue());
  }

  private void assertIdAnchorMessageCreated() {
    if (getEventuateCommonJdbcOperations().getEventuateSqlDialect() instanceof MySqlDialect) {
      List<Map<String, Object>> anchorMessages =
              getEventuateJdbcStatementExecutor().queryForList("select * from eventuate.message where destination = 'CDC-IGNORED'");

      Assert.assertEquals(1, anchorMessages.size());
    }
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

  private List<Map<String, Object>> getEvents(IdColumnAndValue idColumnAndValue) {
    String table = eventuateSchema.qualifyTable("events");
    String sql = String.format("select event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata from %s where %s = ?",
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
  }

  private List<Map<String, Object>> getMessages(IdColumnAndValue idColumnAndValue) {
    String table = eventuateSchema.qualifyTable("message");
    String sql = String.format("select %s, destination, headers, payload, creation_time from %s where %s = ?",
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

    Assert.assertTrue(String.format("Row id should start from current time in milliseconds after migration (current time: %s, id: %s)", currentTime, id),
            currentTime - id < precision);
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
