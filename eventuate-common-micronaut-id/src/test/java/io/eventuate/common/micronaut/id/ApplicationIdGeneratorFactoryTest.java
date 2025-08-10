package io.eventuate.common.micronaut.id;


import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@MicronautTest
public class ApplicationIdGeneratorFactoryTest {

  @Inject
  private IdGenerator idGenerator;

  @Test
  public void testThatApplicationIdGeneratorIsUsed() {
    Assertions.assertEquals(ApplicationIdGenerator.class, idGenerator.getClass());
  }
}