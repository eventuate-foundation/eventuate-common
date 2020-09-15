package io.eventuate.common.jdbc;

import java.sql.SQLException;

public class EventuateSqlException extends RuntimeException {

  public EventuateSqlException(String message) {
    super(message);
  }

  public EventuateSqlException(SQLException e) {
    super(e);
  }

  public EventuateSqlException(Exception e) {
    super(e);
  }
}
