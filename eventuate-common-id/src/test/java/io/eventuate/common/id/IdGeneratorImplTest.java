package io.eventuate.common.id;

import org.junit.Assert;
import org.junit.Test;

public class IdGeneratorImplTest {

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionOnNegativeInstanceId() {
    new IdGeneratorImpl(-1L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionOnTooBigInstanceId() {
    new IdGeneratorImpl(IdGeneratorImpl.SERVICE_ID_MAX_VALUE + 1);
  }

  @Test
  public void shouldGenerateAnId() {
    IdGenerator idGenerator = new IdGeneratorImpl(IdGeneratorImpl.SERVICE_ID_MAX_VALUE / 2);

    Int128 id = idGenerator.genId(Long.MAX_VALUE);

    Assert.assertEquals(IdGeneratorImpl.SERVICE_ID_MAX_VALUE / 2, id.getLo());
    Assert.assertEquals(Long.MAX_VALUE, id.getHi());
  }

  @Test
  public void assertThatServiceIdMaxValueIs48BitsSize() {
    String binaryString = Long.toBinaryString(IdGeneratorImpl.SERVICE_ID_MAX_VALUE);

    Assert.assertEquals(48, binaryString.length());
    Assert.assertTrue(binaryString.chars().allMatch(value -> value == '1'));
  }
}