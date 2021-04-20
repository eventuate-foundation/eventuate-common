package io.eventuate.common.jdbc;

import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;

public abstract class EventuateAbstractJdbcOperations {

  public static final String MESSAGE_AUTO_GENERATED_ID_COLUMN = "dbid";
  public static final String EVENT_AUTO_GENERATED_ID_COLUMN = "id";

  public static final String MESSAGE_APPLICATION_GENERATED_ID_COLUMN = "id";
  public static final String EVENT_APPLICATION_GENERATED_ID_COLUMN = "event_id";

  private EventuateSqlDialect eventuateSqlDialect;

  public EventuateAbstractJdbcOperations(EventuateSqlDialect eventuateSqlDialect) {
    this.eventuateSqlDialect = eventuateSqlDialect;
  }

  public EventuateSqlDialect getEventuateSqlDialect() {
    return eventuateSqlDialect;
  }

  protected String insertIntoEventsTableApplicationIdSql(EventuateSchema eventuateSchema) {
    return String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?);", eventuateSchema.qualifyTable("events"));
  }

  protected String insertIntoEventsTableDbIdSql(EventuateSchema eventuateSchema) {
    return String.format("INSERT INTO %s (event_id, event_type, event_data, entity_type, entity_id, triggering_event, metadata, published)" +
            " VALUES ('', ?, ?, ?, ?, ?, ?, ?);", eventuateSchema.qualifyTable("events"));
  }

  protected String insertIntoMessageTableApplicationIdSql(EventuateSchema eventuateSchema) {
    return insertIntoMessageTable(eventuateSchema,
            "insert into %s(id, destination, headers, payload, creation_time, published) values(?, ?, %s, %s, %s, ?)");
  }

  protected String insertIntoMessageTableDbIdSql(EventuateSchema eventuateSchema) {
    return insertIntoMessageTable(eventuateSchema,
            "insert into %s(id, destination, headers, payload, creation_time, published) values('', ?, %s, %s, %s, ?)");
  }

  private String insertIntoMessageTable(EventuateSchema eventuateSchema, String sql) {
    return String.format(sql,
            eventuateSchema.qualifyTable("message"),
            columnToJson(eventuateSchema, "headers"),
            columnToJson(eventuateSchema, "payload"),
            eventuateSqlDialect.getCurrentTimeInMillisecondsExpression());
  }

  protected int booleanToInt(boolean bool) {
    return bool ? 1 : 0;
  }

  protected String columnToJson(EventuateSchema eventuateSchema, String column) {
    return eventuateSqlDialect.castToJson("?",
            eventuateSchema, "message", column, null /*TODO: postgres requires access to database for conversion*/);
  }
}
