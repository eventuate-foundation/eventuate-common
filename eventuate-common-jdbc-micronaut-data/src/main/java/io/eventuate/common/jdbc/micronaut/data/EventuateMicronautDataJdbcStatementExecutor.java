package io.eventuate.common.jdbc.micronaut.data;

import io.eventuate.common.jdbc.*;
import io.micronaut.data.jdbc.runtime.JdbcOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EventuateMicronautDataJdbcStatementExecutor implements EventuateJdbcStatementExecutor {

  private JdbcOperations jdbcOperations;

  public EventuateMicronautDataJdbcStatementExecutor(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public int update(String sql, Object... parameters) {
    Connection connection = jdbcOperations.getConnection();

    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      for (int i = 1; i <= parameters.length; i++) {
        preparedStatement.setObject(i, parameters[i - 1]);
      }

      return preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      if (EventuateJdbcUtils.isDuplicateKeyException(e.getSQLState(), e.getErrorCode())) {
        throw new EventuateDuplicateKeyException(e);
      }

      throw new EventuateSqlException(e);
    }
  }

  @Override
  public <T> List<T> query(String sql, EventuateRowMapper<T> eventuateRowMapper, Object... parameters) {
    Connection connection = jdbcOperations.getConnection();

    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      for (int i = 1; i <= parameters.length; i++) {
        preparedStatement.setObject(i, parameters[i - 1]);
      }

      ResultSet rs = preparedStatement.executeQuery();

      List<T> result = new ArrayList<>();

      int rowNum = 0;
      while (rs.next()) {
        result.add(eventuateRowMapper.mapRow(rs, rowNum++));
      }

      return result;
    }
    catch (SQLException e) {
      throw new EventuateSqlException(e);
    }
  }

  @Override
  public List<Map<String, Object>> queryForList(String sql, Object... parameters) {
    Connection connection = jdbcOperations.getConnection();

    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      for (int i = 1; i <= parameters.length; i++) {
        preparedStatement.setObject(i, parameters[i - 1]);
      }

      ResultSet rs = preparedStatement.executeQuery();

      List<Map<String, Object>> result = new ArrayList<>();

      while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
          row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
        }
        result.add(row);
      }

      return result;
    }
    catch (SQLException e) {
      throw new EventuateSqlException(e);
    }
  }
}
