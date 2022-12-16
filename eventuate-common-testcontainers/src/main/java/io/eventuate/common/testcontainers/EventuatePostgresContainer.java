package io.eventuate.common.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class EventuatePostgresContainer extends AbstractEventuatePostgresContainer<EventuatePostgresContainer> {

    public EventuatePostgresContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-postgres", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuatePostgresContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
        ;
    }

    @NotNull
    static EventuatePostgresContainer makeFromDockerfile() {
        return new EventuatePostgresContainer(FileSystems.getDefault().getPath("../postgres/Dockerfile"));
    }

    @Override
    public String getEventuateDatabaseSchema() {
        return "eventuate";
    }

    @Override
    public String getMonitoringSchema() {
        return "eventuate";
    }
}
