package io.eventuate.common.jdbc;

public class SqlWithParameters {
  private String sql;
  private Object[] parameters;

  public SqlWithParameters(String sql, Object[] parameters) {
    this.sql = sql;
    this.parameters = parameters;
  }

  public String getSql() {
    return sql;
  }

  public Object[] getParameters() {
    return parameters;
  }
}
