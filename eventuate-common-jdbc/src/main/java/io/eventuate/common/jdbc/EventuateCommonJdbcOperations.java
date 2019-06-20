package io.eventuate.common.jdbc;

import io.eventuate.common.json.mapper.JSonMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {

  private DataSource dataSource;

  public EventuateCommonJdbcOperations(DataSource dataSource) {
    this.dataSource = dataSource;
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

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


      preparedStatement.setString(1, eventId);
      preparedStatement.setString(2, eventType);
      preparedStatement.setString(3, eventData);
      preparedStatement.setString(4, entityType);
      preparedStatement.setString(5, entityId);
      preparedStatement.setString(6, triggeringEvent.orElse(null));
      preparedStatement.setString(7, metadata.orElse(null));

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  public void insertIntoMessageTable(String messageId,
                                      String payload,
                                      String destination,
                                      String currentTimeInMillisecondsSql,
                                      Map<String, String> headers,
                                      EventuateSchema eventuateSchema) {

    String table = eventuateSchema.qualifyTable("message");
    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time) values(?, ?, ?, ?, %s)", table, currentTimeInMillisecondsSql);

    String serializedHeaders = JSonMapper.toJson(headers);

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


      preparedStatement.setString(1, messageId);
      preparedStatement.setString(2, destination);
      preparedStatement.setString(3, serializedHeaders);
      preparedStatement.setString(4, payload);

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
