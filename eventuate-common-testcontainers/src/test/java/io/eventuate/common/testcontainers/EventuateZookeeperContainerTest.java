package io.eventuate.common.testcontainers;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;

public class EventuateZookeeperContainerTest {

    @ClassRule
    public static EventuateZookeeperContainer container = EventuateZookeeperContainer.makeFromDockerfile();

    @Test
    public void shouldStart() {
    }


}