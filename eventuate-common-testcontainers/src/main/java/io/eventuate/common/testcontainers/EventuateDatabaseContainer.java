package io.eventuate.common.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

public abstract class EventuateDatabaseContainer<T extends GenericContainer<T>> extends GenericContainer<T> implements PropertyProvidingContainer {

    protected EventuateDatabaseContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    protected EventuateDatabaseContainer(String dockerImageName) {
        super(dockerImageName);
    }

    protected EventuateDatabaseContainer(ImageFromDockerfile withDockerfile) {
        super(withDockerfile);
    }
}
