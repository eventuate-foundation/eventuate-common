package io.eventuate.common.micronaut.inmemorydatabase;

import io.eventuate.common.common.spring.inmemorydatabase.EventuateInMemoryDataSourceBuilder;
import io.eventuate.common.inmemorydatabase.EventuateDatabaseScriptSupplier;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.List;

@Factory
public class EventuateCommonInMemoryDatabaseFactory {
  @Singleton
  @Replaces(DataSource.class)
  @Named("default")
  public DataSource dataSource(List<EventuateDatabaseScriptSupplier> scripts) {
    return new EventuateInMemoryDataSourceBuilder(scripts).build();
  }
}
