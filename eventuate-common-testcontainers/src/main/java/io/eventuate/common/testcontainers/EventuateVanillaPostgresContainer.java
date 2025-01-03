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

    public static EventuateVanillaPostgresContainer make() {
      return new EventuateVanillaPostgresContainer();
    }

    public static EventuateVanillaPostgresContainer makeFromDockerfile() {
      return new EventuateVanillaPostgresContainer(ContainerUtil.asPath("../postgres/Dockerfile-vanilla"));
    }

    @Override
    public String getEventuateDatabaseSchema() {
        return "public";
    }

    @Override
    public String getMonitoringSchema() {
        return "public";
    }

}
