package io.eventuate.common.jdbc;

public interface EventuateTransactionTemplate {
  void executeInTransaction(Runnable callback);
}
