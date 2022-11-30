package io.eventuate.common.testcontainers;

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

        withEnv("MYSQL_ROOT_PASSWORD", "rootpassword");
        withEnv("MYSQL_USER", "mysqluser");
        withEnv("MYSQL_PASSWORD", "mysqlpw");
        withExposedPorts(3306);
    }

    @Override
    public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
        registry.accept("spring.datasource.url",
                () -> String.format("jdbc:mysql://localhost:%s/eventuate", getFirstMappedPort()));
        registry.accept("spring.datasource.username", () -> "mysqluser");
        registry.accept("spring.datasource.password", () -> "mysqlpw");
    }
}
