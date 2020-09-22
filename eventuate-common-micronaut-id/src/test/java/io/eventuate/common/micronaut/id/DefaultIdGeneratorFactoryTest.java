package io.eventuate.common.micronaut.id;


import io.eventuate.common.id.DefaultIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MicronautTest
public class DefaultIdGeneratorFactoryTest {

  @Inject
  private IdGenerator idGenerator;

  @Test
  public void testThatDefaultIdGeneratorIsUsed() {
    Assertions.assertEquals(DefaultIdGenerator.class, idGenerator.getClass());
  }
}