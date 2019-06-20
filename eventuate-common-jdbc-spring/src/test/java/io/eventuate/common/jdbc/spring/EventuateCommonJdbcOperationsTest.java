package io.eventuate.common.jdbc.spring;

import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.json.mapper.JSonMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.*;

@SpringBootTest(classes = EventuateCommonJdbcOperationsConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventuateCommonJdbcOperationsTest {

  @Autowired
  private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testInsertIntoEventsTable() throws SQLException {
    String eventId = generateId();
    String entityId = generateId();
    String eventData = generateId();
    String eventType = generateId();
    String entityType = generateId();
    String triggeringEvent = generateId();
    String metadata = generateId();
    EventuateSchema eventuateSchema = new EventuateSchema();

    eventuateCommonJdbcOperations.insertIntoEventsTable(eventId,
            entityId, eventData, eventType, entityType, Optional.of(triggeringEvent), Optional.of(metadata), eventuateSchema);

    List<Map<String, Object>> events = jdbcTemplate.queryForList(String.format("select event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata from %s " +
                    "where event_id = ?",
            eventuateSchema.qualifyTable("events")), eventId);

    Assert.assertEquals(1, events.size());

    Map<String, Object> event = events.get(0);

    Assert.assertEquals(eventType, event.get("event_type"));
    Assert.assertEquals(eventData, event.get("event_data"));
    Assert.assertEquals(entityType, event.get("entity_type"));
    Assert.assertEquals(entityId, event.get("entity_id"));
    Assert.assertEquals(triggeringEvent, event.get("triggering_event"));
    Assert.assertEquals(metadata, event.get("metadata"));
  }

  @Test
  public void testInsertIntoMessageTable() throws SQLException {
    String messageId = generateId();
    String payload = generateId();
    String destination = generateId();
    Long time = System.nanoTime();
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("header1k", "header1v");
    headers.put("header2k", "header2v");

    EventuateSchema eventuateSchema = new EventuateSchema();

    eventuateCommonJdbcOperations.insertIntoMessageTable(messageId,
            payload,
            destination,
            time.toString(),
            headers,
            eventuateSchema);

    List<Map<String, Object>> events = jdbcTemplate.queryForList(String.format("select id, destination, headers, payload, creation_time from %s " +
                    "where id = ?",
            eventuateSchema.qualifyTable("message")), messageId);

    Assert.assertEquals(1, events.size());

    Map<String, Object> event = events.get(0);

    Assert.assertEquals(destination, event.get("destination"));
    Assert.assertEquals(payload, event.get("payload"));
    Assert.assertEquals(time, event.get("creation_time"));
    Assert.assertEquals(JSonMapper.toJson(headers), event.get("headers"));
  }

  private String generateId() {
    return UUID.randomUUID().toString();
  }
}
