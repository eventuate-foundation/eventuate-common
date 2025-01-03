package io.eventuate.common.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ContainerUtil {
    public static String findImage(String imageBaseName, String versionPropertiesFile) {
        Properties props = new Properties();
        try {
            props.load(EventuateMySqlContainer.class.getResourceAsStream("/" + versionPropertiesFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.format("%s:%s", imageBaseName, props.getProperty("version"));
    }

    static void registerPortProperty(GenericContainer<?> container, BiConsumer<String, Supplier<Object>> registry, String name) {
        registry.accept(name,
                () -> String.format("localhost:%s", container.getFirstMappedPort()));
    }

    @NotNull
    static Path asPath(String first) {
      return FileSystems.getDefault().getPath(first);
    }
}
