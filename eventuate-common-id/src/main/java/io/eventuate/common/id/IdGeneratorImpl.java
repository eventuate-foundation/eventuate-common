package io.eventuate.common.id;

public class IdGeneratorImpl implements IdGenerator {

  public static final long SERVICE_ID_MAX_VALUE = 0x0000ffffffffffffL;

  private final long serviceId;

  public IdGeneratorImpl(long serviceId) {
    this.serviceId = serviceId;

    if (serviceId < 0 || serviceId > SERVICE_ID_MAX_VALUE) {
      throw new IllegalArgumentException(String.format("service id should be between 0 and %s", SERVICE_ID_MAX_VALUE));
    }
  }

  @Override
  public Int128 genId(long databaseId) {
    return new Int128(databaseId, serviceId);
  }
}
