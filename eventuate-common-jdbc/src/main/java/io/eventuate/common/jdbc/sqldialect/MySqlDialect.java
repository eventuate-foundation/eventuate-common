package io.eventuate.common.jdbc.sqldialect;

import io.eventuate.common.jdbc.JdbcUrlParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

public class MySqlDialect extends AbstractEventuateSqlDialect {

  public MySqlDialect() {
    super(Optional.of("com.mysql.cj.jdbc.Driver"),
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("mysql", "mariadb"))),
            "ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000)");
  }

  @Override
  public String getJdbcCatalogue(String dataSourceUrl) {
    return JdbcUrlParser.parse(dataSourceUrl).getDatabase();
  }
}
