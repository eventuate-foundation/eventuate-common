package io.eventuate.common.jdbc.micronaut;

import java.sql.Connection;
import java.util.function.Consumer;

public interface EventuateMicronautTransactionManagement {
  void doWithTransaction(Consumer<Connection> callback);
}
