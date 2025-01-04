package io.eventuate.common.flyway;


import org.springframework.boot.jdbc.DatabaseDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

public class TemplatedMessageTableCreator {

  private final ScriptExecutor scriptExecutor;

  public TemplatedMessageTableCreator() {
    this.scriptExecutor = new ScriptExecutor();
  }

  public void migrate(org.flywaydb.core.api.migration.Context context, List<String> suffixes) throws Exception {

    Connection connection = context.getConnection();

    DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(connection.getMetaData().getURL());
    String driverId = driver.getId();

    SqlExecutor sqlExecutor = statement -> {
      try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        preparedStatement.execute();
      }
    };

    suffixes.forEach(suffix ->
        scriptExecutor.executeScript(Collections.singletonMap("EVENTUATE_OUTBOX_SUFFIX", suffix),
            "flyway-templates/" + driverId + "/3.create-message-table.sql", sqlExecutor));
  }

}
