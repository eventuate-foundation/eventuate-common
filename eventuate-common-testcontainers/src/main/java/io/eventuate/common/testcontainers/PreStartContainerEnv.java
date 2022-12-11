package io.eventuate.common.testcontainers;

import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PreStartContainerEnv {
    private Map<String, Supplier<String>> preStartEnv = new HashMap<>();

    public PreStartContainerEnv() {
    }

    public void withEnv(String name, Supplier<String> valueSupplier) {
        this.preStartEnv.put(name, valueSupplier);
    }

    public void preStartConfiguration(GenericContainer<?> container) {
        preStartEnv.forEach((name, valueSupplier) -> container.withEnv(name, valueSupplier.get()));
    }
}