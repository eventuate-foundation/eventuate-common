package io.eventuate.common.quarkus.inmemorydatabase;

import io.eventuate.common.inmemorydatabase.EventuateDatabaseScriptSupplier;
import io.eventuate.common.inmemorydatabase.EventuateInMemoryDataSourceBuilder;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.util.stream.Collectors;

@ApplicationScoped
@Alternative
@Priority(0)
public class EventuateCommonInMemoryDatabaseConfiguration {
  @Produces
  public DataSource dataSource(Instance<EventuateDatabaseScriptSupplier> scripts) {
    return new EventuateInMemoryDataSourceBuilder(scripts.stream().collect(Collectors.toList())).build();
  }
}
