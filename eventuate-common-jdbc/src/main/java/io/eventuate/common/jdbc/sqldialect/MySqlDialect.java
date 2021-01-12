package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.JdbcUrlParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class MySqlDialect extends AbstractEventuateSqlDialect {

  public MySqlDialect() {
    super(new HashSet<>(Arrays.asList("com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver")),
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("mysql", "mariadb"))),
            "ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000)");
  }

  @Override
  public String getJdbcCatalogue(String dataSourceUrl) {
    return JdbcUrlParser.parse(dataSourceUrl).getDatabase();
  }
}
