package io.eventuate.common.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EventuateZookeeperContainer extends EventuateGenericContainer<EventuateZookeeperContainer> implements PropertyProvidingContainer {

    public EventuateZookeeperContainer() {
        super(ContainerUtil.findImage("eventuateio/eventuate-zookeeper", "eventuate.common.version.properties"));
        withConfiguration();
    }

    public EventuateZookeeperContainer(String image) {
        super(image);
        withConfiguration();
    }

    public EventuateZookeeperContainer(Path path) {
        super(new ImageFromDockerfile().withDockerfile(path));
        withConfiguration();
    }

    @NotNull
    static EventuateZookeeperContainer makeFromDockerfile() {
        return new EventuateZookeeperContainer(FileSystems.getDefault().getPath("../zookeeper/Dockerfile"));
    }

    private void withConfiguration() {
        withExposedPorts(2181);
        waitingFor(Wait.forHealthcheck());
    }

    @Override
    protected int getPort() {
        return 2181;
    }

    @Override
    public void registerProperties(BiConsumer<String, Supplier<Object>> registry) {
        ContainerUtil.registerPortProperty(this, registry, "eventuatelocal.zookeeper.connection.string");
    }

    public String getZookeeperConnect() {
        return "%s:%d".formatted(getHost(), getMappedPort(2181));
    }

}
