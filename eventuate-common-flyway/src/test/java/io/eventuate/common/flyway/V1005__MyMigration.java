package io.eventuate.common.flyway;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.util.List;

public class V1005__MyMigration extends BaseJavaMigration {

  private final List<String> suffixes;
  private final TemplatedMessageTableCreator templatedMessageTableCreator;

  public V1005__MyMigration(TemplatedMessageTableCreator templatedMessageTableCreator, List<String> suffixes) {
    this.suffixes = suffixes;
    this.templatedMessageTableCreator = templatedMessageTableCreator;
  }

  @Override
  public void migrate(Context context) throws Exception {
    templatedMessageTableCreator.migrate(context, suffixes);
  }

}
