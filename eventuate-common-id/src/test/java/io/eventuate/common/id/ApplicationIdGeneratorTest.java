package io.eventuate.common.id;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ApplicationIdGeneratorTest {

  private ApplicationIdGenerator idGen;

  @Before
  public void setUp() {
    idGen = new ApplicationIdGenerator();
  }

  @Test
  public void shouldGenerateId() {
    Int128 id = idGen.genId();
    assertNotNull(id);
  }

  @Test
  public void shouldGenerateMonotonicId() {
    Int128 id1 = idGen.genId();
    Int128 id2 = idGen.genId();
    assertTrue(id1.compareTo(id2) < 0);
  }

  @Test
  public void shouldGenerateLotsOfIds() throws InterruptedException {
    IntStream.range(1, 1000000).forEach(x -> idGen.genId());
    TimeUnit.SECONDS.sleep(1);
    IntStream.range(1, 1000000).forEach(x -> idGen.genId());
  }

}