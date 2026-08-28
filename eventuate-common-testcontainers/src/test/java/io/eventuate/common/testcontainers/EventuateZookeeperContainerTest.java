package io.eventuate.common.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EventuateZookeeperContainerTest {

    public static EventuateZookeeperContainer container = EventuateZookeeperContainer.makeFromDockerfile();

    @BeforeAll
    public static void startContainer() {
        container.start();
    }

    @Test
    public void shouldStart() {
    }


}
