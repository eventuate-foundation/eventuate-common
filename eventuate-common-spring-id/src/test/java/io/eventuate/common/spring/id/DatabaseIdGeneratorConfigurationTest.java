package io.eventuate.common.spring.id;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = IdGeneratorConfiguration.class, properties = "eventuatelocal.cdc.reader.id=1")
@RunWith(SpringJUnit4ClassRunner.class)
public class DatabaseIdGeneratorConfigurationTest {

  @Autowired
  private IdGenerator idGenerator;

  @Test
  public void testThatDatabaseIdGeneratorIsUsed() {
    Assert.assertEquals(DatabaseIdGenerator.class, idGenerator.getClass());
  }
}