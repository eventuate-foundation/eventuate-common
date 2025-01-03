package io.eventuate.common.testcontainers;

import java.util.function.Predicate;

class PostgresDatabaseType implements DatabaseContainerType {
  public static final DatabaseContainerType TYPE = new PostgresDatabaseType();

  @Override
  public boolean supports(Predicate<String> isProfileActive) {
    return isProfileActive.test("postgres");
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaContainer() {
    return EventuateVanillaPostgresContainer.make();
  }

  @Override
  public String getDatabaseType() {
    return "postgres";
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> make() {
    return EventuatePostgresContainer.make();
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeFromDockerfile() {
    return EventuatePostgresContainer.makeFromDockerfile();
  }

  @Override
  public EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaContainerFromDockerfile() {
    return EventuateVanillaPostgresContainer.makeFromDockerfile();
  }
}
