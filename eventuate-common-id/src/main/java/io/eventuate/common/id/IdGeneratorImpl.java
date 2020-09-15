package io.eventuate.common.id;

public class IdGeneratorImpl implements IdGenerator {

  public static final long SERVICE_ID_MAX_VALUE = 0x0000ffffffffffffL;

  private Long serviceId;

  public IdGeneratorImpl(Long serviceId) {
    this.serviceId = serviceId;

    if (serviceId < 0 || serviceId > SERVICE_ID_MAX_VALUE) {
      throw new IllegalArgumentException(String.format("service id should be between 0 and %s", SERVICE_ID_MAX_VALUE));
    }
  }

  @Override
  public synchronized Int128 genId(Long databaseId) {
    return new Int128(databaseId, serviceId);
  }
}
