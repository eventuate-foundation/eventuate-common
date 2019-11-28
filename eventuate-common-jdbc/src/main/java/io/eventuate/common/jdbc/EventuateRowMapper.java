package io.eventuate.common.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EventuateRowMapper<T> {
  T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
