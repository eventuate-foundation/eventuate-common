package io.eventuate.common.testcontainers;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class AbstractEventuatePostgresContainer<T extends AbstractEventuatePostgresContainer<T>> extends EventuateDatabaseContainer<T> {
    public AbstractEventuatePostgresContainer(String dockerImageName) {
        super(dockerImageName);
    }

    public AbstractEventuatePostgresContainer(ImageFromDockerfile withDockerfile) {
        super(withDockerfile);
    }

    protected void withConfiguration() {
        String eventuateOutboxId = System.getProperty("eventuate.outbox.id");
        if (eventuateOutboxId != null)
            withEnv("USE_DB_ID", "true");
        withEnv("POSTGRES_DB", "eventuate");
        withEnv("POSTGRES_USER", "postgresuser");
        withEnv("POSTGRES_PASSWORD", "postgrespw");
        withExposedPorts(5432);
        waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2));
    }

    @Override
    protected int getPort() {
        return 5432;
    }

    @Override
    public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
        registry.accept("spring.datasource.url", this::getLocalJdbcUrl);
        registry.accept("spring.datasource.username", () -> "postgresuser");
        registry.accept("spring.datasource.password", () -> "postgrespw");
        registry.accept("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.accept("eventuate.database.schema", this::getEventuateDatabaseSchema);

    }

    @Override
    public DatabaseCredentials getCredentials() {
        return new DatabaseCredentials("postgresuser", "postgrespw");
    }

    @Override
    public String getLocalJdbcUrl() {
        return String.format("jdbc:postgresql://localhost:%s/eventuate", getFirstMappedPort());
    }

    @Override
    public String getJdbcUrl() {
        return String.format("jdbc:postgresql://%s:5432/eventuate", getFirstNetworkAlias());
    }

    @Override
    public String getDatabaseName() {
        return "eventuate";
    }

    @Override
    public DatabaseCredentials getAdminCredentials() {
        return getCredentials();
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getCdcReaderType() {
        return "postgres-wal";
    }
}
