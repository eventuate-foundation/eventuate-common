package io.eventuate.common.inmemorydatabase;

import io.micronaut.context.annotation.Factory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Factory
public class EventuateCommonInMemoryDatabaseFactory {

  @Singleton
  public DataSource dataSource(List<EventuateDatabaseScriptSupplier> scripts) {
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    builder.setType(EmbeddedDatabaseType.H2);
    scripts.stream().flatMap( s -> s.get().stream()).collect(Collectors.toSet()).forEach(builder::addScript);
    return builder.build();
  }

}
