package io.eventuate.common.testcontainers;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class AbstractEventuateMySqlContainer<T extends AbstractEventuateMySqlContainer<T>> extends EventuateDatabaseContainer<T> {
    public AbstractEventuateMySqlContainer(String dockerImageName) {
        super(dockerImageName);
    }

    public AbstractEventuateMySqlContainer(ImageFromDockerfile withDockerfile) {
        super(withDockerfile);
    }

    protected void withConfiguration() {
        String eventuateOutboxId = System.getProperty("eventuate.outbox.id");
        if (eventuateOutboxId != null)
            withEnv("USE_DB_ID", "true");

        withEnv("MYSQL_DATABASE", "eventuate");
        withEnv("MYSQL_ROOT_PASSWORD", "rootpassword");
        withEnv("MYSQL_USER", "mysqluser");
        withEnv("MYSQL_PASSWORD", "mysqlpw");
        withExposedPorts(3306);
        waitingFor(Wait.forHealthcheck());
    }

    @Override
    protected int getPort() {
        return 3306;
    }

    @Override
    public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
        registry.accept("spring.datasource.url", this::getLocalJdbcUrl);
        registry.accept("spring.datasource.username", () -> "mysqluser");
        registry.accept("spring.datasource.password", () -> "mysqlpw");
        registry.accept("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.accept("eventuate.database.schema", this::getEventuateDatabaseSchema);
    }

    @Override
    public DatabaseCredentials getCredentials() {
        return new DatabaseCredentials("mysqluser", "mysqlpw");
    }

    @Override
    public String getLocalJdbcUrl() {
        return "jdbc:mysql://localhost:%s/eventuate".formatted(getFirstMappedPort());
    }

    public String getJdbcUrl() {
        return "jdbc:mysql://%s:3306/eventuate".formatted(getFirstNetworkAlias());
    }

    @Override
    public String getDatabaseName() {
        return "eventuate";
    }

    @Override
    public DatabaseCredentials getAdminCredentials() {
        return new DatabaseCredentials("root", "rootpassword");
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
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
        return "mysql-binlog";
    }
}
