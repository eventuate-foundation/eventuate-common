package io.eventuate.common.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EventuateZookeeperContainer extends GenericContainer<EventuateZookeeperContainer> implements PropertyProvidingContainer {

    public EventuateZookeeperContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-zookeeper", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuateZookeeperContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

    private void withConfiguration() {
        withExposedPorts(2181);
    }

    @Override
    public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
        ContainerUtil.registerPortProperty(this, registry, "eventuatelocal.zookeeper.connection.string");
    }

    public String getZookeeperConnect() {
        return String.format("%s:%d", getHost(), getMappedPort(2181));
    }

}