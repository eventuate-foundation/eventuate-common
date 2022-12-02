package io.eventuate.common.testcontainers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;

public class EventuateVanillaPostgresContainer extends AbstractEventuatePostgresContainer<EventuateVanillaPostgresContainer> {

    public EventuateVanillaPostgresContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-vanilla-postgres", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuateVanillaPostgresContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

}
