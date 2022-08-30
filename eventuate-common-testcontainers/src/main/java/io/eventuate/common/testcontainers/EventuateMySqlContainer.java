package io.eventuate.common.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EventuateMySqlContainer extends GenericContainer<EventuateMySqlContainer> implements PropertyProvidingContainer {

    public EventuateMySqlContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-mysql8", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuateMySqlContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

    private void withConfiguration() {
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
