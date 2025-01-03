package io.eventuate.common.testcontainers;

import java.util.Arrays;
import java.util.List;

public class DatabaseContainerFactory {

  private static final List<DatabaseContainerType> dataContainerTypes =
      Arrays.asList(MsSqlDatabaseType.TYPE, PostgresDatabaseType.TYPE, MySqlDatabaseType.TYPE);

  private static DatabaseContainerType findDatabaseContainerType() {
    return dataContainerTypes.stream().filter(type -> type.supports(DatabaseContainerFactory::isProfileActive))
        .findFirst().orElseThrow(() -> new RuntimeException("No available database container type found"));
  }

  private static boolean isProfileActive(String name) {
    return contains(System.getenv("SPRING_PROFILES_ACTIVE"), name) || contains(System.getProperty("spring.profiles.active"), name);
  }

  private static boolean contains(String value, String substring) {
    return value != null && value.contains(substring);
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeDatabaseContainer() {
    return findDatabaseContainerType().make();
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaDatabaseContainer() {
    return findDatabaseContainerType().makeVanillaContainer();
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeDatabaseContainerFromDockerFile() {
    return findDatabaseContainerType().makeFromDockerfile();
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaDatabaseContainerFromDockerFile() {
    return findDatabaseContainerType().makeVanillaContainerFromDockerfile();
  }


  public static String getDatabaseType() {
    return findDatabaseContainerType().getDatabaseType();
  }
}
