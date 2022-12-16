package io.eventuate.common.testcontainers;

import org.junit.ClassRule;
import org.junit.Test;

import java.nio.file.FileSystems;

public class EventuateZookeeperContainerTest {

    @ClassRule
    public static EventuateZookeeperContainer container = new EventuateZookeeperContainer(FileSystems.getDefault().getPath("../zookeeper/Dockerfile"));

    @Test
    public void shouldStart() {
    }


}