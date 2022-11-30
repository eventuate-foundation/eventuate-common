package io.eventuate.common.testcontainers;

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

        withEnv("POSTGRES_USER", "postgresuser");
        withEnv("POSTGRES_PASSWORD", "postgrespw");
        withExposedPorts(5432);
    }

    @Override
    public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
        registry.accept("spring.datasource.url",
                () -> String.format("jdbc:postgresql://localhost:%s/eventuate", getFirstMappedPort()));
        registry.accept("spring.datasource.username", () -> "postgresuser");
        registry.accept("spring.datasource.password", () -> "postgrespw");
    }
}
