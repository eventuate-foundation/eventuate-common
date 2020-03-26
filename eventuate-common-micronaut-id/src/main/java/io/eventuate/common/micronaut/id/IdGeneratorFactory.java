package io.eventuate.common.micronaut.id;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.IdGeneratorImpl;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class IdGeneratorFactory {

  @Singleton
  public IdGenerator idGenerator() {
    return new IdGeneratorImpl();
  }

}
