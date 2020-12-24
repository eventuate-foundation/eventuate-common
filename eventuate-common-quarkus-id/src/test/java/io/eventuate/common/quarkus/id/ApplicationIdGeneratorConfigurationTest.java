package io.eventuate.common.quarkus.id;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
public class ApplicationIdGeneratorConfigurationTest {

  @Inject
  IdGenerator idGenerator;

  @Test
  public void testThatApplicationIdGeneratorIsUsed() {
    Assertions.assertEquals(ApplicationIdGenerator.class, idGenerator.getClass());
  }
}