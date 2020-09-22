package io.eventuate.common.id;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ApplicationIdGeneratorTest {

  @Test
  public void shouldGenerateId() {
    ApplicationIdGenerator idGen = new ApplicationIdGenerator();
    Int128 id = idGen.genId(null);
    assertNotNull(id);
  }

  @Test
  public void shouldGenerateMonotonicId() {
    ApplicationIdGenerator idGen = new ApplicationIdGenerator();
    Int128 id1 = idGen.genId(null);
    Int128 id2 = idGen.genId(null);
    assertTrue(id1.compareTo(id2) < 0);
  }

  @Test
  public void shouldGenerateLotsOfIds() throws InterruptedException {
    ApplicationIdGenerator idGen = new ApplicationIdGenerator();
    IntStream.range(1, 1000000).forEach(x -> idGen.genId(null));
    TimeUnit.SECONDS.sleep(1);
    IntStream.range(1, 1000000).forEach(x -> idGen.genId(null));
  }

}