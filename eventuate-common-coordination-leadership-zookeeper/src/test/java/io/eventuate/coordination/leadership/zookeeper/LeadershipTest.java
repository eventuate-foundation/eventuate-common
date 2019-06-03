package io.eventuate.coordination.leadership.zookeeper;

import io.eventuate.coordination.leadership.tests.AbstractLeadershipTest;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeadershipTest.Config.class)
public class LeadershipTest extends AbstractLeadershipTest<ZkLeaderSelector> {

  @EnableAutoConfiguration
  public static class Config {
  }

  @Value("${eventuatelocal.zookeeper.connection.string}")
  private String zkUrl;

  private String lockId;

  @Before
  public void init() {
    lockId = String.format("/zk/lock/test/%s", UUID.randomUUID().toString());
  }

  @Override
  protected ZkLeaderSelector createLeaderSelector(Runnable leaderSelectedCallback, Runnable leaderRemovedCallback) {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkUrl,
            new ExponentialBackoffRetry(1000, 5));
    curatorFramework.start();

    return new ZkLeaderSelector(curatorFramework,
            lockId,
            leaderSelectedCallback,
            leaderRemovedCallback);
  }
}
