package io.eventuate.common.id;

import java.util.Optional;

public class DatabaseIdGenerator implements IdGenerator {

  public static final long SERVICE_ID_MAX_VALUE = 0x0000ffffffffffffL;
  public static final long COUNTER_MAX_VALUE = 0xffffL;

  private final long serviceId;

  @Override
  public boolean databaseIdRequired() {
    return true;
  }

  public DatabaseIdGenerator(long serviceId) {
    this.serviceId = serviceId;

    if (serviceId < 0 || serviceId > SERVICE_ID_MAX_VALUE) {
      throw new IllegalArgumentException("service id should be between 0 and %s".formatted(SERVICE_ID_MAX_VALUE));
    }
  }

  @Override
  public Int128 genId(Long databaseId, Integer partitionOffset) {

    if (databaseId == null) {
      throw new IllegalArgumentException("database id is required");
    }

    return new Int128(databaseId, serviceId + (partitionOffset == null ? 0 : partitionOffset));
  }

  @Override
  public Optional<Int128> incrementIdIfPossible(Int128 anchorId) {
    long counter = anchorId.getLo() >>> 48;

    if (counter == COUNTER_MAX_VALUE) {
      return Optional.empty();
    }

    counter = (++counter) << 48;

    long lo = anchorId.getLo() & 0x0000ffffffffffffL | counter;

    return Optional.of(new Int128(anchorId.getHi(), lo));
  }
}
