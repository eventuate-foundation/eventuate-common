package io.eventuate.common.micronaut.id;

import io.eventuate.common.id.DefaultIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.ImprovedIdGenerator;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;

@Factory
public class IdGeneratorFactory {

  @Singleton
  @Requires(missingProperty = "eventuatelocal.cdc.reader.id")
  public IdGenerator defaultIdGenerator() {
    return new DefaultIdGenerator();
  }

  @Singleton
  @Requires(property = "eventuatelocal.cdc.reader.id")
  public IdGenerator improvedIdGenerator(@Value("${eventuatelocal.cdc.reader.id}") long id) {
    return new ImprovedIdGenerator(id);
  }
}