package io.eventuate.common.jdbc.micronaut.data;

import io.eventuate.common.jdbc.EventuateTransactionTemplate;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.function.Supplier;

@Singleton
public class EventuateMicronautDataTransactionTemplate implements EventuateTransactionTemplate {

  @Transactional
  public <T> T executeInTransaction(Supplier<T> callback) {
    return callback.get();
  };
}
