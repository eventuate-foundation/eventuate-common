package io.eventuate.common.testcontainers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;

public class EventuateVanillaMySqlContainer extends AbstractEventuateMySqlContainer<EventuateVanillaMySqlContainer> {

    public EventuateVanillaMySqlContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-vanilla-mysql8", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuateVanillaMySqlContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

}
