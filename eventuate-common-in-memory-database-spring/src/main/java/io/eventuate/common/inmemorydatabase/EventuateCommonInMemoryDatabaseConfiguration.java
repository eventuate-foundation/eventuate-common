package io.eventuate.common.inmemorydatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class EventuateCommonInMemoryDatabaseConfiguration {

  @Autowired(required=false)
  private List<EventuateDatabaseScriptSupplier> scripts = Collections.emptyList();

  @Bean
  public DataSource dataSource() {
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    builder.setType(EmbeddedDatabaseType.H2);
    scripts.stream().flatMap( s -> s.get().stream()).collect(Collectors.toSet()).forEach(builder::addScript);
    return builder.build();
  }

}
