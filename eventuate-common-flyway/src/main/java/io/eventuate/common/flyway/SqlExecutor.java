package io.eventuate.common.flyway;

import java.sql.SQLException;

interface SqlExecutor {
  void execute(String ddl) throws SQLException;
}
