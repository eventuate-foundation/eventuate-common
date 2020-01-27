package io.eventuate.common.inmemorydatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
public class EventuateCommonInMemoryDatabaseConfiguration {
  @Bean
  public DataSource dataSource(@Autowired(required=false) List<EventuateDatabaseScriptSupplier> scripts) {
    return new EventuateInMemoryDataSourceBuilder(Optional.ofNullable(scripts).orElse(Collections.emptyList())).build();
  }
}
