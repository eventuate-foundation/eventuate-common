package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.JdbcUrlParser;

public class MySqlDialect extends DefaultEventuateSqlDialect {

  public MySqlDialect() {
    super("ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000)");
  }

  @Override
  public boolean supports(String driver) {
    return "com.mysql.cj.jdbc.Driver".equals(driver);
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }

  @Override
  public String getJdbcCatalogue(String dataSourceUrl) {
    return JdbcUrlParser.parse(dataSourceUrl).getDatabase();
  }
}
