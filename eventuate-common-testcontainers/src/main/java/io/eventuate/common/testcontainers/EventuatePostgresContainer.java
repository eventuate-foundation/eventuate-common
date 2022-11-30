package io.eventuate.common.testcontainers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EventuatePostgresContainer extends AbstractEventuatePostgresContainer {

    public EventuatePostgresContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-postgres", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuatePostgresContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

}
