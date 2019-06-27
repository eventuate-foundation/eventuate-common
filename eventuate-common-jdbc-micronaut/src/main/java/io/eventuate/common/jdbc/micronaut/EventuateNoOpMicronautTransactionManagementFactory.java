package io.eventuate.common.jdbc.micronaut;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class EventuateNoOpMicronautTransactionManagementFactory {

  @Singleton
  @Requires(missingBeans = EventuateMicronautTransactionManagement.class)
  public EventuateMicronautTransactionManagement eventuateMicronautTransactionManagement(DataSource dataSource) {
    return new EventuateNoOpMicronautTransactionManagement(dataSource);
  }
}
