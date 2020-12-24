package io.eventuate.common.quarkus.jdbc;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.function.Supplier;

@ApplicationScoped
public class EventuateQuarkusTransactionTemplate {

  @Transactional
  public <T> T executeInTransaction(Supplier<T> callback) {
    return callback.get();
  };
}
