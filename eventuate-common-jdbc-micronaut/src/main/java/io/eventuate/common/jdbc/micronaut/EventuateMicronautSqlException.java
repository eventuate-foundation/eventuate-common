package io.eventuate.common.jdbc.micronaut;

import java.sql.SQLException;

public class EventuateMicronautSqlException extends RuntimeException {
  public EventuateMicronautSqlException(SQLException sqlException) {
    super(sqlException);
  }
}
