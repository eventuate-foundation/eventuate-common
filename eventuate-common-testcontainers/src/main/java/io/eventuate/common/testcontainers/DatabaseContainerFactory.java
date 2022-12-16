package io.eventuate.common.testcontainers;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class DatabaseContainerFactory {
  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeDatabaseContainer() {
    return isPostgres() ? makePostgresContainer() : makeMySqlContainer();
  }

  private static EventuateMySqlContainer makeMySqlContainer() {
    return new EventuateMySqlContainer();
  }

  private static EventuatePostgresContainer makePostgresContainer() {
    return new EventuatePostgresContainer();
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaDatabaseContainer() {
    return isPostgres() ? makeVanillaPostgresContainer() : makeVanillaMySqlContainer();
  }

  protected static EventuateVanillaMySqlContainer makeVanillaMySqlContainer() {
    return new EventuateVanillaMySqlContainer();
  }

  public static EventuateVanillaPostgresContainer makeVanillaPostgresContainer() {
    return new EventuateVanillaPostgresContainer();
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeDatabaseContainerFromDockerFile() {
    return isPostgres() ?
            new EventuatePostgresContainer(asPath("../postgres/Dockerfile"))
            : new EventuateMySqlContainer(asPath("../mysql/Dockerfile-mysql8"));
  }

  public static EventuateDatabaseContainer<? extends EventuateDatabaseContainer<?>> makeVanillaDatabaseContainerFromDockerFile() {
    return isPostgres() ? makeVanillaPostgresContainerFromDockerfile()
            : makeVanillaMySqlContainerFromDockerfile();
  }

  public static EventuateVanillaMySqlContainer makeVanillaMySqlContainerFromDockerfile() {
    return new EventuateVanillaMySqlContainer(asPath("../mysql/Dockerfile-vanilla-mysql8"));
  }

  public static EventuateVanillaPostgresContainer makeVanillaPostgresContainerFromDockerfile() {
    return new EventuateVanillaPostgresContainer(asPath("../postgres/Dockerfile-vanilla"));
  }

  @NotNull
  private static Path asPath(String first) {
    return FileSystems.getDefault().getPath(first);
  }


  public static boolean isPostgres() {
    return contains(System.getenv("SPRING_PROFILES_ACTIVE"), "postgres") || contains(System.getProperty("spring.profiles.active"), "postgres");
  }

  private static boolean contains(String value, String substring) {
    return value != null && value.contains(substring);
  }

  public static String getDatabaseType() {
    return isPostgres() ? "postgres" : "mysql";
  }
}
