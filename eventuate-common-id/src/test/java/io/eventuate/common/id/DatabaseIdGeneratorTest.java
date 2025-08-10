package io.eventuate.common.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseIdGeneratorTest {

  private final long SERVICE_ID = DatabaseIdGenerator.SERVICE_ID_MAX_VALUE / 2;

  @Test
  public void shouldThrowAnExceptionOnNegativeInstanceId() {
    assertThrows(IllegalArgumentException.class, () -> {
      new DatabaseIdGenerator(-1L);
    });
  }

  @Test
  public void shouldThrowAnExceptionOnTooBigInstanceId() {
    assertThrows(IllegalArgumentException.class, () -> {
      new DatabaseIdGenerator(DatabaseIdGenerator.SERVICE_ID_MAX_VALUE + 1);
    });
  }

  @Test
  public void shouldGenerateAnId() {
    IdGenerator idGenerator = new DatabaseIdGenerator(SERVICE_ID);

    Int128 id = idGenerator.genId(Long.MAX_VALUE, null);
    Assertions.assertEquals(SERVICE_ID, id.getLo());
    Assertions.assertEquals(Long.MAX_VALUE, id.getHi());
  }

  @Test
  public void shouldGenerateAnIdWithPartitionOffset() {
    IdGenerator idGenerator = new DatabaseIdGenerator(SERVICE_ID);
    int partitionOffset = 1;

    Int128 id = idGenerator.genId(Long.MAX_VALUE, partitionOffset);
    Assertions.assertEquals(SERVICE_ID + partitionOffset, id.getLo());
    Assertions.assertEquals(Long.MAX_VALUE, id.getHi());
  }

  @Test
  public void assertThatServiceIdMaxValueIs48BitsSize() {
    String binaryString = Long.toBinaryString(DatabaseIdGenerator.SERVICE_ID_MAX_VALUE);

    Assertions.assertEquals(48, binaryString.length());
    Assertions.assertTrue(binaryString.chars().allMatch(value -> value == '1'));
  }

  @Test
  public void testIdIncrement() {
    DatabaseIdGenerator databaseIdGenerator = new DatabaseIdGenerator(0);

    // counter is 0, should become 1
    Int128 id = new Int128(0, 0);
    Assertions.assertEquals(Optional.of(new Int128(0, 0b0000000000000001000000000000000000000000000000000000000000000000L)),
            databaseIdGenerator.incrementIdIfPossible(id));

    // counter is 2^16-1, id should be regenerated
    id = new Int128(0, 0b1111111111111111000000000000000000000000000000000000000000000000L);
    Assertions.assertEquals(Optional.empty(), databaseIdGenerator.incrementIdIfPossible(id));

    // counter is 2^16-2, should become 2^16-1
    id = new Int128(0, 0b1111111111111110000000000000000000000000000000000000000000000000L);
    Assertions.assertEquals(Optional.of(new Int128(0, 0b1111111111111111000000000000000000000000000000000000000000000000L)),
            databaseIdGenerator.incrementIdIfPossible(id));
  }
}