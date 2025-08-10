package io.eventuate.common.jdbc;

import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;

public class EventuateJdbcOperationsUtils {

  public static final String MESSAGE_AUTO_GENERATED_ID_COLUMN = "dbid";
  public static final String EVENT_AUTO_GENERATED_ID_COLUMN = "id";

  public static final String MESSAGE_APPLICATION_GENERATED_ID_COLUMN = "id";
  public static final String EVENT_APPLICATION_GENERATED_ID_COLUMN = "event_id";

  private EventuateSqlDialect eventuateSqlDialect;

  public EventuateJdbcOperationsUtils(EventuateSqlDialect eventuateSqlDialect) {
    this.eventuateSqlDialect = eventuateSqlDialect;
  }


  public String insertIntoEventsTableApplicationIdSql(EventuateSchema eventuateSchema) {
    return String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?)", eventuateSchema.qualifyTable("events"));
  }

  public String insertIntoEventsTableDbIdSql(EventuateSchema eventuateSchema) {
    return String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)" +
            " VALUES ('', ?, ?, ?, ?, ?, ?, ?)", eventuateSchema.qualifyTable("events"));
  }

  public String insertIntoMessageTableApplicationIdSql(EventuateSchema eventuateSchema, SqlJsonConverter jsonConverter, String outboxTableSuffix) {
    return insertIntoMessageTable(eventuateSchema,
            "insert into %s%s(id, destination, headers, payload, creation_time, published, message_partition) values(?, ?, %s, %s, %s, ?, ?)",
            jsonConverter, outboxTableSuffix);
  }

  public String insertIntoMessageTableDbIdSql(EventuateSchema eventuateSchema, SqlJsonConverter jsonConverter, String outboxTableSuffix) {
    return insertIntoMessageTable(eventuateSchema,
            "insert into %s%s(id, destination, headers, payload, creation_time, published, message_partition) values('', ?, %s, %s, %s, ?, ?)",
            jsonConverter, outboxTableSuffix);
  }

  public String insertIntoMessageTable(EventuateSchema eventuateSchema, String sql, SqlJsonConverter jsonConverter, String outboxTableSuffix) {
    return sql.formatted(
            eventuateSchema.qualifyTable("message"),
            outboxTableSuffix,
            jsonConverter.convert(eventuateSchema, "headers"),
            jsonConverter.convert(eventuateSchema, "payload"),
            eventuateSqlDialect.getCurrentTimeInMillisecondsExpression());
  }

  public int booleanToInt(boolean bool) {
    return bool ? 1 : 0;
  }
}
