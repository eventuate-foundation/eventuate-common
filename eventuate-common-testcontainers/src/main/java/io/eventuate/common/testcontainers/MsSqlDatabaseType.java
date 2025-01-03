package io.eventuate.common.testcontainers;

import java.util.function.Predicate;

class MsSqlDatabaseType implements DatabaseContainerType {
  public static final DatabaseContainerType TYPE = new MsSqlDatabaseType();

  @Override
  public boolean supports(Predicate<String> isProfileActive) {
    return isProfileActive.test("mssql");
  }


  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaContainer() {
    return EventuateVanillaMsSqlContainer.make();
  }

  @Override
  public String getDatabaseType() {
    return "mssql";
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> make() {
    throw new UnsupportedOperationException();
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeFromDockerfile() {
    throw new UnsupportedOperationException();
  }


  @Override
  public EventuateVanillaMsSqlContainer makeVanillaContainerFromDockerfile() {
    return EventuateVanillaMsSqlContainer.make();
  }
}
