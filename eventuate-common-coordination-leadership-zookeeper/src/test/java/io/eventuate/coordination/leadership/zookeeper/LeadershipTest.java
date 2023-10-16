package io.eventuate.coordination.leadership.zookeeper;

import io.eventuate.common.testcontainers.EventuateZookeeperContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import io.eventuate.coordination.leadership.LeaderSelectedCallback;
import io.eventuate.coordination.leadership.tests.AbstractLeadershipTest;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@SpringBootTest(classes = LeadershipTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class LeadershipTest extends AbstractLeadershipTest<ZkLeaderSelector> {

  @Configuration
  @EnableAutoConfiguration
  public static class Config {
  }

  public static EventuateZookeeperContainer zookeeper = new EventuateZookeeperContainer("eventuateio/eventuate-zookeeper:0.19.0.BUILD-SNAPSHOT")
          .withReuse(true)
          .withNetworkAliases("zookeeper");

  @DynamicPropertySource
  static void registerContainerProperties(DynamicPropertyRegistry registry) {
    PropertyProvidingContainer.startAndProvideProperties(registry, zookeeper);
  }


  @Value("${eventuatelocal.zookeeper.connection.string}")
  private String zkUrl;

  private String lockId;

  @Before
  public void init() {
    lockId = String.format("/zk/lock/test/%s", UUID.randomUUID());
  }

  @Override
  protected ZkLeaderSelector createLeaderSelector(LeaderSelectedCallback leaderSelectedCallback, Runnable leaderRemovedCallback) {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkUrl,
            new ExponentialBackoffRetry(1000, 5));
    curatorFramework.start();

    return new ZkLeaderSelector(curatorFramework,
            lockId,
            leaderSelectedCallback,
            leaderRemovedCallback);
  }
}
