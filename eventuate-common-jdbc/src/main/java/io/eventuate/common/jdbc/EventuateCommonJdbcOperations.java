package io.eventuate.common.jdbc;

import java.util.Map;
import java.util.Optional;

public class EventuateCommonJdbcOperations {

  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;

  public EventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor) {
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
  }

  public void insertIntoEventsTable(String eventId,
                                    String entityId,
                                    String eventData,
                                    String eventType,
                                    String entityType,
                                    Optional<String> triggeringEvent,
                                    Optional<String> metadata,
                                    EventuateSchema eventuateSchema) {

   SqlWithParameters sqlWithParameters = EventuateJdbcUtils.createInsertIntoEventsTableSql(eventId,
           entityId,
           eventData,
           eventType,
           entityType,
           triggeringEvent,
           metadata,
           eventuateSchema);

    eventuateJdbcStatementExecutor.update(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
  }


  public void insertIntoMessageTable(String messageId,
                                      String payload,
                                      String destination,
                                      String currentTimeInMillisecondsSql,
                                      Map<String, String> headers,
                                      EventuateSchema eventuateSchema) {

    SqlWithParameters sqlWithParameters = EventuateJdbcUtils.createInsertIntoMessageTableSql(messageId,
            payload,
            destination,
            currentTimeInMillisecondsSql,
            headers,
            eventuateSchema);

    eventuateJdbcStatementExecutor.update(sqlWithParameters.getSql(), sqlWithParameters.getParameters());
  }
}
