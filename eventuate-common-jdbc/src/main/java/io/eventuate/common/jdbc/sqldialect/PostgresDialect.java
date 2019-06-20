package io.eventuate.common.jdbc.sqldialect;

public class PostgresDialect extends DefaultEventuateSqlDialect {

  public PostgresDialect() {
    super("(ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000))");
  }

  @Override
  public boolean supports(String driver) {
    return "org.postgresql.Driver".equals(driver);
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }
}
