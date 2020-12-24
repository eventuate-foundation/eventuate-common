package io.eventuate.common.quarkus.id;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;

@ApplicationScoped
public class IdGeneratorConfiguration {
  @Produces
  public IdGenerator idGenerator(@ConfigProperty(name = "eventuate.outbox.id") Optional<Long> id) {
    return id
            .map(DatabaseIdGenerator::new)
            .map(generator -> (IdGenerator)generator)
            .orElseGet(ApplicationIdGenerator::new);
  }
}