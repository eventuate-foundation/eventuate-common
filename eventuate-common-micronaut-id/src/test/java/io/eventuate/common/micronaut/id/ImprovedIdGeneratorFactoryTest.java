package io.eventuate.common.micronaut.id;


import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.ImprovedIdGenerator;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MicronautTest(propertySources = "classpath:improved.id.generator.test.yml")
public class ImprovedIdGeneratorFactoryTest {

  @Inject
  private IdGenerator idGenerator;

  @Test
  public void testThatDefaultIdGeneratorIsUsed() {
    Assertions.assertEquals(ImprovedIdGenerator.class, idGenerator.getClass());
  }
}