package io.eventuate.common.inmemorydatabase;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class EventuateInMemoryDataSourceBuilder {
  private List<EventuateDatabaseScriptSupplier> scripts;

  public EventuateInMemoryDataSourceBuilder(List<EventuateDatabaseScriptSupplier> scripts) {
    this.scripts = scripts;
  }

  public DataSource build() {
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    builder.setType(EmbeddedDatabaseType.H2);
    scripts.stream().flatMap( s -> s.get().stream()).collect(Collectors.toSet()).forEach(builder::addScript);
    return builder.build();
  }
}
