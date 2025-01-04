package io.eventuate.common.testcontainers;

import java.util.Arrays;
import java.util.List;

public class DatabaseContainerTypeRegistry {
  static final List<DatabaseContainerType> dataContainerTypes =
      Arrays.asList(MsSqlDatabaseType.TYPE, PostgresDatabaseType.TYPE, MySqlDatabaseType.TYPE);

  static DatabaseContainerType findDatabaseContainerType() {
    String springProfilesActiveEnv = System.getenv("SPRING_PROFILES_ACTIVE");
    String springProfilesActiveProperty = System.getProperty("spring.profiles.active");
    return findDatabaseContainerType(springProfilesActiveEnv, springProfilesActiveProperty);
  }

  static DatabaseContainerType findDatabaseContainerType(String springProfilesActiveEnv, String springProfilesActiveProperty) {
    return dataContainerTypes.stream().filter(type -> type.supports(name -> eitherContains(name, springProfilesActiveEnv, springProfilesActiveProperty)))
        .findFirst().orElseThrow(() -> new RuntimeException("No available database container type found"));
  }

  private static boolean eitherContains(String name, String springProfilesActiveEnv, String springProfilesActiveProperty) {
    return contains(springProfilesActiveEnv, name) || contains(springProfilesActiveProperty, name);
  }

  private static boolean contains(String value, String substring) {
    return value != null && value.contains(substring);
  }
}
