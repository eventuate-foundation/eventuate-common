package io.eventuate.common.testcontainers;

import java.util.function.Predicate;

class MySqlDatabaseType implements DatabaseContainerType {

  public static final DatabaseContainerType TYPE = new MySqlDatabaseType();

  @Override
  public boolean supports(Predicate<String> isProfileActive) {
    return true; // It's the default
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaContainer() {
    return EventuateVanillaMySqlContainer.make();
  }

  @Override
  public String getDatabaseType() {
    return "mysql";
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> make() {
    return EventuateMySqlContainer.make();
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeFromDockerfile() {
    return EventuateMySqlContainer.makeFromDockerfile();
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaContainerFromDockerfile() {
    return EventuateVanillaMySqlContainer.makeFromDockerfile();
  }
}
