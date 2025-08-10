package io.eventuate.common.spring.id;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = IdGeneratorConfiguration.class, properties = "eventuate.outbox.id=1")
public class DatabaseIdGeneratorConfigurationTest {

  @Autowired
  private IdGenerator idGenerator;

  @Test
  public void testThatDatabaseIdGeneratorIsUsed() {
    Assertions.assertEquals(DatabaseIdGenerator.class, idGenerator.getClass());
  }
}