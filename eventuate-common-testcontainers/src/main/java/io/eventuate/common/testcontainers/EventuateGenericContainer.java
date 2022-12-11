package io.eventuate.common.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Supplier;

public abstract class EventuateGenericContainer<T extends EventuateGenericContainer<T>> extends GenericContainer<T> {
    private final PreStartContainerEnv preStartContainerEnv = new PreStartContainerEnv();
    private String firstNetworkAlias;

    public EventuateGenericContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public EventuateGenericContainer(String dockerImageName) {
        super(dockerImageName);
    }

    public EventuateGenericContainer(ImageFromDockerfile dockerFile) {
        super(dockerFile);
    }

    public String getFirstNetworkAlias() {
        return firstNetworkAlias;
    }

    @Override
    public T withNetworkAliases(String... aliases) {
        firstNetworkAlias = aliases[0];
        return super.withNetworkAliases(aliases);
    }

    public String getConnectionString() {
        return getFirstNetworkAlias() + ":" + getPort();
    }

    protected abstract int getPort();

    public T withEnv(String name, Supplier<String> valueSupplier) {
        preStartContainerEnv.withEnv(name, valueSupplier);
        return (T) this;
    }
    @Override
    public void start() {
        preStartContainerEnv.preStartConfiguration(this);
        super.start();
    }

}
