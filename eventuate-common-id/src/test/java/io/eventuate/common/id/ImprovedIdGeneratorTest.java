package io.eventuate.common.id;

import org.junit.Assert;
import org.junit.Test;

public class ImprovedIdGeneratorTest {

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionOnNegativeInstanceId() {
    new ImprovedIdGenerator(-1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionOnTooBigInstanceId() {
    new ImprovedIdGenerator(ImprovedIdGenerator.SERVICE_ID_MAX_VALUE + 1);
  }

  @Test
  public void shouldGenerateAnId() {
    IdGenerator idGenerator = new ImprovedIdGenerator(ImprovedIdGenerator.SERVICE_ID_MAX_VALUE / 2);

    Int128 id = idGenerator.genId(Long.MAX_VALUE);

    Assert.assertEquals(ImprovedIdGenerator.SERVICE_ID_MAX_VALUE / 2, id.getLo());
    Assert.assertEquals(Long.MAX_VALUE, id.getHi());
  }

  @Test
  public void assertThatServiceIdMaxValueIs48BitsSize() {
    String binaryString = Long.toBinaryString(ImprovedIdGenerator.SERVICE_ID_MAX_VALUE);

    Assert.assertEquals(48, binaryString.length());
    Assert.assertTrue(binaryString.chars().allMatch(value -> value == '1'));
  }
}