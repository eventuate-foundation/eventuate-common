package io.eventuate.common.jdbc.micronaut;

import java.sql.SQLException;

public interface EventuateMicronautTransactionManagement {
  void doWithConnection(TransactionCallback connection) throws SQLException;
}
