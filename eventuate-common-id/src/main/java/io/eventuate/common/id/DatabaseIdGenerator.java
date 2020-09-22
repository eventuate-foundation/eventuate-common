package io.eventuate.common.id;

public class DatabaseIdGenerator implements IdGenerator {

  public static final long SERVICE_ID_MAX_VALUE = 0x0000ffffffffffffL;

  private final long serviceId;

  @Override
  public boolean databaseIdRequired() {
    return true;
  }

  public DatabaseIdGenerator(long serviceId) {
    this.serviceId = serviceId;

    if (serviceId < 0 || serviceId > SERVICE_ID_MAX_VALUE) {
      throw new IllegalArgumentException(String.format("service id should be between 0 and %s", SERVICE_ID_MAX_VALUE));
    }
  }

  @Override
  public Int128 genId(Long databaseId) {

    if (databaseId == null) {
      throw new IllegalArgumentException("database id is required");
    }

    return new Int128(databaseId, serviceId);
  }
}
