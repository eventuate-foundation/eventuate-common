package io.eventuate.common.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class DatabaseContainerFactory {
  public static EventuateDatabaseContainer makeDatabaseContainer() {
    return isPostgres() ? makePostgresContainer() : makeMySqlContainer();
  }

  private static EventuateMySqlContainer makeMySqlContainer() {
    return configureMySql(new EventuateMySqlContainer());
  }

  private static EventuatePostgresContainer makePostgresContainer() {
    return configurePostgres(new EventuatePostgresContainer());
  }

  public static EventuateDatabaseContainer makeVanillaDatabaseContainer() {
    return isPostgres() ? makeVanillaPostgresContainer() : makeVanillaMySqlContainer();
  }

  protected static EventuateVanillaMySqlContainer makeVanillaMySqlContainer() {
    return configureMySql(new EventuateVanillaMySqlContainer());
  }

  public static EventuateVanillaPostgresContainer makeVanillaPostgresContainer() {
    return configurePostgres(new EventuateVanillaPostgresContainer());
  }

  public static EventuateDatabaseContainer makeDatabaseContainerFromDockerFile() {
    return isPostgres() ?
            configurePostgres(new EventuatePostgresContainer(asPath("../postgres/Dockerfile")))
            : configureMySql(new EventuateMySqlContainer(asPath("../mysql/Dockerfile-mysql8")));
  }

  public static EventuateDatabaseContainer makeVanillaDatabaseContainerFromDockerFile() {
    return isPostgres() ? makeVanillaPostgresContainerFromDockerfile()
            : makeVanillaMySqlContainerFromDockerfile();
  }

  public static EventuateVanillaMySqlContainer makeVanillaMySqlContainerFromDockerfile() {
    return configureMySql(new EventuateVanillaMySqlContainer(asPath("../mysql/Dockerfile-vanilla-mysql8")));
  }

  public static EventuateVanillaPostgresContainer makeVanillaPostgresContainerFromDockerfile() {
    return configurePostgres(new EventuateVanillaPostgresContainer(asPath("../postgres/Dockerfile-vanilla")));
  }

  @NotNull
  private static Path asPath(String first) {
    return FileSystems.getDefault().getPath(first);
  }

  private static <T extends EventuateDatabaseContainer<T>> T configureMySql(T mysqlContainer) {
    return  mysqlContainer
            // This needs to be changed - eventuate is hardwired
            .withEnv("MYSQL_DATABASE", "eventuate")
            .withReuse(true)
            .waitingFor(Wait.forHealthcheck());
  }

  private static <T extends EventuateDatabaseContainer<T>> T configurePostgres(T postgresContainer) {
    return  postgresContainer
            // What's the right thing to do here
            .withEnv("POSTGRES_DB", "eventuate")
            .withReuse(true)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2));
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
