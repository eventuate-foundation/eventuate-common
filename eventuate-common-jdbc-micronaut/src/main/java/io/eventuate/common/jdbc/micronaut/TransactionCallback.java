package io.eventuate.common.jdbc.micronaut;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionCallback {
  void execute(Connection connection) throws SQLException;
}
