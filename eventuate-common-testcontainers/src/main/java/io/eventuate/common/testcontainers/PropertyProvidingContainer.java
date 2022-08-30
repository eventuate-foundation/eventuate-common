package io.eventuate.common.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface PropertyProvidingContainer {
    void registerProperties(BiConsumer<String, Supplier<Object>> registry);
    void start();

    static void startAndProvideProperties(DynamicPropertyRegistry registry, PropertyProvidingContainer... containers) {
        Arrays.stream(containers).forEach(container -> {
            container.start();
            container.registerProperties(registry::add);
        });
    }
}
