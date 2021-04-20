package io.eventuate.common.jdbc;

import java.sql.SQLException;

public class EventuateDuplicateKeyException extends EventuateSqlException {
  public EventuateDuplicateKeyException(SQLException sqlException) {
    super(sqlException);
  }

  public EventuateDuplicateKeyException(Throwable throwable) {
    super(throwable);
  }
}
