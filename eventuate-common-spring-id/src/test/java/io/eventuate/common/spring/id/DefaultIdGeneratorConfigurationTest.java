package io.eventuate.common.spring.id;

import io.eventuate.common.id.DefaultIdGenerator;
import io.eventuate.common.id.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = IdGeneratorConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultIdGeneratorConfigurationTest {

  @Autowired
  private IdGenerator idGenerator;

  @Test
  public void testThatDefaultIdGeneratorIsUsed() {
    Assert.assertEquals(DefaultIdGenerator.class, idGenerator.getClass());
  }

}