package io.eventuate.common.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Import(EventuateSchemaConfiguration.class) // SqlDialectConfiguration too?
public class EventuateCommonJdbcConfiguration {

  @Bean
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(JdbcTemplate jdbcTemplate) {
    return new EventuateCommonJdbcOperations(jdbcTemplate);
  }

}
