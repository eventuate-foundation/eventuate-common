package io.eventuate.common.testcontainers;

import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.FileSystems;

public class DatabaseContainerFactory {
  public static EventuateDatabaseContainer makeDatabaseContainer() {
    return isPostgres() ?
            new EventuatePostgresContainer(FileSystems.getDefault().getPath("../postgres/Dockerfile"))
                    // What's the right thing to do here
                    .withEnv("POSTGRES_DB", "eventuate")
                    .withReuse(true)
                    .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2))
            :
            new EventuateMySqlContainer(FileSystems.getDefault().getPath("../mysql/Dockerfile"))
                    // This needs to be changed - eventuate is hardwired
                    .withEnv("MYSQL_DATABASE", "eventuate")
                    .withReuse(true)
                    .waitingFor(Wait.forHealthcheck());
  }

  public static EventuateDatabaseContainer makeVanillaDatabaseContainer() {
    return isPostgres() ?
            new EventuateVanillaPostgresContainer(FileSystems.getDefault().getPath("../postgres/Dockerfile"))
                    // What's the right thing to do here
                    .withEnv("POSTGRES_DB", "eventuate")
                    .withReuse(true)
                    .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2))
            :
            new EventuateVanillaMySqlContainer(FileSystems.getDefault().getPath("../mysql/Dockerfile"))
                    // This needs to be changed - eventuate is hardwired
                    .withEnv("MYSQL_DATABASE", "eventuate")
                    .withReuse(true)
                    .waitingFor(Wait.forHealthcheck());
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
