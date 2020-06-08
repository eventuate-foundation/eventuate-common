package io.eventuate.common.jdbc.sqldialect;

import org.postgresql.util.PGobject;

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

  @Override
  public String castToJson(String sqlPart) {
    return sqlPart + "::json";
  }

  @Override
  public String objectToString(Object object) {
    if (object instanceof PGobject) {
      PGobject pGobject = (PGobject) object;
      return pGobject.getValue();
    }
    return object.toString();
  }
}
