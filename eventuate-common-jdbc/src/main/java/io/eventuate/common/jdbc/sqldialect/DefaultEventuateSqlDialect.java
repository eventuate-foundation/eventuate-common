package io.eventuate.common.jdbc.sqldialect;

import java.util.Collections;

public class DefaultEventuateSqlDialect extends AbstractEventuateSqlDialect {

  public DefaultEventuateSqlDialect(String customCurrentTimeInMillisecondsExpression) {
    super(Collections.emptySet(), Collections.emptySet(), customCurrentTimeInMillisecondsExpression);
  }

  @Override
  public boolean supports(String driver) {
    return true;
  }

  @Override
  public boolean accepts(String name) {
    return true;
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
