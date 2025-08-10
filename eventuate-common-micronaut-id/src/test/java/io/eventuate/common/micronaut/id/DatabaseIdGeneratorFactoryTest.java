package io.eventuate.common.micronaut.id;


import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@MicronautTest(propertySources = "classpath:database.id.generator.test.yml")
public class DatabaseIdGeneratorFactoryTest {

  @Inject
  private IdGenerator idGenerator;

  @Test
  public void testThatDatabaseIdGeneratorIsUsed() {
    Assertions.assertEquals(DatabaseIdGenerator.class, idGenerator.getClass());
  }
}