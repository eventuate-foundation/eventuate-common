package io.eventuate.common.spring.jdbc;

import io.eventuate.common.common.spring.jdbc.EventuateSpringJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateJdbcOperationsUtils;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.sqldialect.EventuateSqlDialect;
import io.eventuate.common.jdbc.sqldialect.SqlDialectSelector;
import io.eventuate.common.spring.jdbc.sqldialect.SqlDialectConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Import({EventuateSchemaConfiguration.class,
        SqlDialectConfiguration.class,
        EventuateTransactionTemplateConfiguration.class})
public class EventuateCommonJdbcOperationsConfiguration {

  @Bean
  public EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor(JdbcTemplate jdbcTemplate) {
    return new EventuateSpringJdbcStatementExecutor(jdbcTemplate);
  }

  @Bean
  public EventuateCommonJdbcOperations eventuateCommonJdbcOperations(EventuateJdbcStatementExecutor eventuateJdbcStatementExecutor,
                                                                     SqlDialectSelector sqlDialectSelector,
                                                                     @Value("${spring.datasource.driver-class-name}") String driver) {
    EventuateSqlDialect eventuateSqlDialect = sqlDialectSelector.getDialect(driver);

    return new EventuateCommonJdbcOperations(new EventuateJdbcOperationsUtils(eventuateSqlDialect),
            eventuateJdbcStatementExecutor, eventuateSqlDialect);
  }
}
