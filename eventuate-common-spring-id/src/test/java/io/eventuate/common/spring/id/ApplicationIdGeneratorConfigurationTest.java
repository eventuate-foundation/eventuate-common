package io.eventuate.common.spring.id;

import io.eventuate.common.id.ApplicationIdGenerator;
import io.eventuate.common.id.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = IdGeneratorConfiguration.class)
public class ApplicationIdGeneratorConfigurationTest {

  @Autowired
  private IdGenerator idGenerator;

  @Test
  public void testThatApplicationIdGeneratorIsUsed() {
    Assertions.assertEquals(ApplicationIdGenerator.class, idGenerator.getClass());
  }

}