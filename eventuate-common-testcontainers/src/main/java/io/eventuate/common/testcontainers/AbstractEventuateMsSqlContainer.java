package io.eventuate.common.testcontainers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class AbstractEventuateMsSqlContainer<T extends AbstractEventuateMsSqlContainer<T>> extends EventuateDatabaseContainer<T> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractEventuateMsSqlContainer.class);

  public static final int DB_PORT = 1433;
  public static final String DB_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  public static final String DB_USER_ID = "sa";
  public static final String DB_PASSWORD = "Eventuate123!";

  public AbstractEventuateMsSqlContainer(String dockerImageName) {
    super(dockerImageName);
  }

  public AbstractEventuateMsSqlContainer(ImageFromDockerfile withDockerfile) {
    super(withDockerfile);
  }

  protected void withConfiguration() {
    String eventuateOutboxId = System.getProperty("eventuate.outbox.id");
    if (eventuateOutboxId != null)
      withEnv("USE_DB_ID", "true");

    withEnv("MSSQL_SA_PASSWORD", DB_PASSWORD);
    withEnv("ACCEPT_EULA", "Y");
    withExposedPorts(DB_PORT);
  }

  @Override
  protected int getPort() {
    return DB_PORT;
  }

    /*
    spring.datasource.url=
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=
     */

  @Override
  public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
    registry.accept("spring.datasource.url", this::getLocalJdbcUrl);
    registry.accept("spring.datasource.username", () -> DB_USER_ID);
    registry.accept("spring.datasource.password", () -> DB_PASSWORD);
    registry.accept("spring.datasource.driver-class-name", () -> DB_DRIVER_CLASS);
    registry.accept("eventuate.database.schema", this::getEventuateDatabaseSchema);
  }

  @Override
  public DatabaseCredentials getCredentials() {
    return new DatabaseCredentials(DB_USER_ID, DB_PASSWORD);
  }

  @Override
  public String getLocalJdbcUrl() {
    return getLocalJdbcUrlWithoutDatabase() + ";databaseName=eventuate";
  }

  private @NotNull String getLocalJdbcUrlWithoutDatabase() {
    return String.format("jdbc:sqlserver://localhost:%s;encrypt=true;trustServerCertificate=true", getFirstMappedPort());
  }

  public String getJdbcUrl() {
    return String.format("jdbc:sqlserver://%s:%s;databaseName=eventuate;encrypt=true;trustServerCertificate=true", getFirstNetworkAlias(), DB_PORT);
  }

  @Override
  public String getDatabaseName() {
    return "eventuate";
  }

  @Override
  public DatabaseCredentials getAdminCredentials() {
    return new DatabaseCredentials(DB_USER_ID, DB_PASSWORD);
  }

  @Override
  public String getDriverClassName() {
    return DB_DRIVER_CLASS;
  }

  @Override
  public String getEventuateDatabaseSchema() {
    return "eventuate";
  }

  @Override
  public String getMonitoringSchema() {
    return "eventuate";
  }

  @Override
  public String getCdcReaderType() {
    return "polling";
  }

  protected void containerIsStarted(InspectContainerResponse containerInfo) {
    super.containerIsStarted(containerInfo);
    createDatabaseAndSchema();
  }

  private void createDatabaseAndSchema() {
    waitForDbToBeReady();

    createDatabase();

    //createEventuateSchema();
  }

  private void createDatabase() {
    try (Connection connection = DriverManager.getConnection(getLocalJdbcUrlWithoutDatabase(), DB_USER_ID, DB_PASSWORD)) {
      connection.createStatement().executeUpdate("create database eventuate");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

//  private void createEventuateSchema() {
//    try (Connection connection = DriverManager.getConnection(getLocalJdbcUrl(), DB_USER_ID, DB_PASSWORD)) {
//      connection.createStatement().executeUpdate("create schema eventuate");
//    } catch (SQLException e) {
//      throw new RuntimeException(e);
//    }
//  }

  private void waitForDbToBeReady() {
    for (int i = 0; i < 10; i++) {
      try {
        try (Connection connection = DriverManager.getConnection(getLocalJdbcUrlWithoutDatabase(), DB_USER_ID, DB_PASSWORD)) {
          connection.createStatement().executeQuery("select 1");
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        break;
      } catch (Exception e) {
        logger.info("Waiting for database to be ready {}", i);
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e1) {
          throw new RuntimeException(e1);
        }
      }
    }
  }

  private boolean isPortOpen(String host, int port) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), 2000);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

}
