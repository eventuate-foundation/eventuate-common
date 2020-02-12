package io.eventuate.common.jdbc;


import io.eventuate.common.json.mapper.JSonMapper;

import java.sql.SQLException;
import java.util.*;

public class EventuateJdbcUtils {

  private static final Set DUPLICATE_KEY_ERROR_CODES = new HashSet<>(Arrays.asList(
          1062, // MySQL
          2601,2627, // MS-SQL
          23505, // Postgres
          23001 // H2
  ));

  public static boolean isDuplicateKeyException(String sqlState, int errorCode) {
    Optional<Integer> additionalErrorCode = Optional.empty();

    // Workaround for postgres, where e.getErrorCode() is 0
    try {
      additionalErrorCode = Optional.of(Integer.parseInt(sqlState));
    } catch (NumberFormatException nfe) {
      // ignore
    }

    return  (DUPLICATE_KEY_ERROR_CODES.contains(errorCode) ||
            additionalErrorCode.map(DUPLICATE_KEY_ERROR_CODES::contains).orElse(false));

  }

  public static SqlWithParameters createInsertIntoEventsTableSql(String eventId,
                                                                 String entityId,
                                                                 String eventData,
                                                                 String eventType,
                                                                 String entityType,
                                                                 Optional<String> triggeringEvent,
                                                                 Optional<String> metadata,
                                                                 EventuateSchema eventuateSchema) {

    String table = eventuateSchema.qualifyTable("events");
    String sql = String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata) VALUES (?, ?, ?, ?, ?, ?, ?)", table);

    return new SqlWithParameters(sql, new Object[]{eventId, eventType, eventData, entityType, entityId, triggeringEvent.orElse(null), metadata.orElse(null)});
  }


  public static SqlWithParameters createInsertIntoMessageTableSql(String messageId,
                                                                  String payload,
                                                                  String destination,
                                                                  String currentTimeInMillisecondsSql,
                                                                  Map<String, String> headers,
                                                                  EventuateSchema eventuateSchema) {

    String table = eventuateSchema.qualifyTable("message");
    String sql = String.format("insert into %s(id, destination, headers, payload, creation_time) values(?, ?, ?, ?, %s)", table, currentTimeInMillisecondsSql);
    String serializedHeaders = JSonMapper.toJson(headers);

    return new SqlWithParameters(sql, new Object[]{messageId, destination, serializedHeaders, payload});
  }
}
