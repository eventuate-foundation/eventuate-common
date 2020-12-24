package io.eventuate.common.quarkus.jdbc;

import io.eventuate.common.jdbc.EventuateSchema;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;

@ApplicationScoped
public class EventuateSchemaConfiguration {
  @Produces
  public EventuateSchema eventuateSchema(@ConfigProperty(name = "eventuate.database.schema") Optional<String> eventuateDatabaseSchema) {
    return new EventuateSchema(eventuateDatabaseSchema.orElse(null));
  }
}
