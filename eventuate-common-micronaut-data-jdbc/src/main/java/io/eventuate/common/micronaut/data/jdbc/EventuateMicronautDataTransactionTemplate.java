package io.eventuate.common.micronaut.data.jdbc;

import io.eventuate.common.jdbc.EventuateTransactionTemplate;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.function.Supplier;

@Singleton
public class EventuateMicronautDataTransactionTemplate implements EventuateTransactionTemplate {

  @Transactional
  public <T> T executeInTransaction(Supplier<T> callback) {
    return callback.get();
  };
}
