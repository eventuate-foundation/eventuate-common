package io.eventuate.common.micronaut.data.jdbc;

import io.eventuate.common.jdbc.EventuateDuplicateKeyException;
import io.eventuate.common.jdbc.EventuateJdbcStatementExecutor;
import io.eventuate.common.jdbc.EventuateRowMapper;
import io.eventuate.common.jdbc.EventuateSqlException;
import io.micronaut.data.jdbc.runtime.JdbcOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class EventuateMicronautDataJdbcStatementExecutor implements EventuateJdbcStatementExecutor {

  private static final Set DUPLICATE_KEY_ERROR_CODES = new HashSet<>(Arrays.asList(
          1062, // MySQL
          2601,2627, // MS-SQL
          23505, // Postgres
          23001 // H2
  ));

  private JdbcOperations jdbcOperations;

  public EventuateMicronautDataJdbcStatementExecutor(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public long insertAndReturnGeneratedId(String sql, String idColumn, Object... parameters) {
    Connection connection = jdbcOperations.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      for (int i = 1; i <= parameters.length; i++) {
        preparedStatement.setObject(i, parameters[i - 1]);
      }

      preparedStatement.executeUpdate();

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getLong(1);
        }
        else {
          throw new EventuateSqlException("Id was not generated");
        }
      }
    } catch (SQLException e) {
      handleSqlUpdateException(e);

      return -1; //should not be here
    }
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
      handleSqlUpdateException(e);

      return 0; //should not be here
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

  private void handleSqlUpdateException(SQLException e) {
    Optional<Integer> additionalErrorCode = Optional.empty();

    // Workaround for postgres, where e.getErrorCode() is 0
    try {
      additionalErrorCode = Optional.of(Integer.parseInt(e.getSQLState()));
    } catch (NumberFormatException nfe) {
      // ignore
    }

    if (DUPLICATE_KEY_ERROR_CODES.contains(e.getErrorCode()) ||
            additionalErrorCode.map(DUPLICATE_KEY_ERROR_CODES::contains).orElse(false)) {

      throw new EventuateDuplicateKeyException(e);
    }

    throw new EventuateSqlException(e);
  }
}
