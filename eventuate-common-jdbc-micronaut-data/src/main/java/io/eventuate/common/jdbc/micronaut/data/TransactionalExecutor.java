package io.eventuate.common.jdbc.micronaut.data;

import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class TransactionalExecutor {

  @Transactional
  public void executeInTransaction(Runnable callback) {
    callback.run();
  }

}
