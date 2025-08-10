package io.eventuate.common.micronaut.jdbc;

import io.eventuate.common.jdbc.EventuateSchema;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.annotation.Nullable;

import jakarta.inject.Singleton;

@Factory
public class EventuateSchemaFactory {

  @Singleton
  public EventuateSchema eventuateSchema(@Nullable @Value("${eventuate.database.schema}") String eventuateDatabaseSchema) {
    return new EventuateSchema(eventuateDatabaseSchema);
  }

}
