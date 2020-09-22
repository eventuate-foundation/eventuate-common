package io.eventuate.common.id;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class DefaultIdGeneratorTest {

  @Test
  public void shouldGenerateId() {
    DefaultIdGenerator idGen = new DefaultIdGenerator();
    Int128 id = idGen.genId(null);
    assertNotNull(id);
  }

  @Test
  public void shouldGenerateMonotonicId() {
    DefaultIdGenerator idGen = new DefaultIdGenerator();
    Int128 id1 = idGen.genId(null);
    Int128 id2 = idGen.genId(null);
    assertTrue(id1.compareTo(id2) < 0);
  }

  @Test
  public void shouldGenerateLotsOfIds() throws InterruptedException {
    DefaultIdGenerator idGen = new DefaultIdGenerator();
    IntStream.range(1, 1000000).forEach(x -> idGen.genId(null));
    TimeUnit.SECONDS.sleep(1);
    IntStream.range(1, 1000000).forEach(x -> idGen.genId(null));
  }

}