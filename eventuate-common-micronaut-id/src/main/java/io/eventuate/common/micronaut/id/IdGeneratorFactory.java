package io.eventuate.common.micronaut.id;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;

import jakarta.inject.Singleton;

@Factory
public class IdGeneratorFactory {

  @Singleton
  @Requires(missingProperty = "eventuate.outbox.id")
  public IdGenerator idGenerator() {
    return new ApplicationIdGenerator();
  }

  @Singleton
  @Requires(property = "eventuate.outbox.id")
  public IdGenerator idGenerator(@Value("${eventuate.outbox.id}") long id) {
    return new DatabaseIdGenerator(id);
  }
}