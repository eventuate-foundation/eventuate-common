package io.eventuate.common.testcontainers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;

public class EventuateMySqlContainer extends AbstractEventuateMySqlContainer<EventuateMySqlContainer> {

    public EventuateMySqlContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-mysql8", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuateMySqlContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

}
