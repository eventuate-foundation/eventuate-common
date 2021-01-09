package io.eventuate.common.jdbc.sqldialect;

import java.util.Collections;
import java.util.Optional;

public class MsSqlDialect extends AbstractEventuateSqlDialect {

  public MsSqlDialect() {
    super(Optional.of("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
            Collections.singleton("mssql"), "(SELECT DATEDIFF_BIG(ms, '1970-01-01', GETUTCDATE()))");
  }

  @Override
  public String addLimitToSql(String sql, String limitExpression) {
    String newSql = sql.replaceFirst("(?i:select)", String.format("select top (%s)", limitExpression));
    if (newSql.equals(sql))
      throw new IllegalArgumentException("Didn't replace in " + newSql);
    return newSql;
  }
}
