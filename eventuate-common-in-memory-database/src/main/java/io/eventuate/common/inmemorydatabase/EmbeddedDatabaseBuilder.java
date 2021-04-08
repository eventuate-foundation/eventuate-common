package io.eventuate.common.inmemorydatabase;

import io.eventuate.common.inmemorydatabase.EventuateDatabaseScriptSupplier;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateTransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;


public class EmbeddedDatabaseBuilder {
  private Stream<EventuateDatabaseScriptSupplier> eventuateDatabaseScriptSuppliers;
  private EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor;
  private EventuateTransactionTemplate eventuateTransactionTemplate;

  public EmbeddedDatabaseBuilder(Stream<EventuateDatabaseScriptSupplier> eventuateDatabaseScriptSuppliers,
                                 EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                 EventuateTransactionTemplate eventuateTransactionTemplate) {

    this.eventuateDatabaseScriptSuppliers = eventuateDatabaseScriptSuppliers;
    this.eventuateJdbcStatementExecutor = eventuateJdbcStatementExecutor;
    this.eventuateTransactionTemplate = eventuateTransactionTemplate;
  }

  public void build() {
    eventuateDatabaseScriptSuppliers
            .flatMap(s -> s.get().stream())
            .forEach(script -> {
              eventuateTransactionTemplate.executeInTransaction(() -> {
                Arrays
                        .stream(loadScriptAsString(script).split(";"))
                        .forEach(eventuateJdbcStatementExecutor::update);

                return null;
              });
            });
  }

  private String loadScriptAsString(String script) {
    ClassLoader classLoader = getClass().getClassLoader();

    try(BufferedReader br = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(script)))) {

      StringBuilder sb = new StringBuilder();
      String s;
      while ((s = br.readLine()) != null) {
        sb.append(s);
      }

      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
