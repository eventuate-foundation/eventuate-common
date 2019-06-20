package io.eventuate.common.jdbc.sqldialect;

public class MySqlDialect extends DefaultEventuateSqlDialect {

  public MySqlDialect() {
    super("ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000)");
  }

  @Override
  public boolean supports(String driver) {
    return "com.mysql.jdbc.Driver".equals(driver);
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }
}
