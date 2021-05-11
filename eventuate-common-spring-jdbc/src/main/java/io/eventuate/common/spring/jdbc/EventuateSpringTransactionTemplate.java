package io.eventuate.common.spring.jdbc;

import io.eventuate.common.jdbc.EventuateTransactionTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public class EventuateSpringTransactionTemplate implements EventuateTransactionTemplate {

  private TransactionTemplate transactionTemplate;

  public EventuateSpringTransactionTemplate(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  public <T> T executeInTransaction(Supplier<T> callback) {
    return transactionTemplate.execute(status -> callback.get());
  }
}
