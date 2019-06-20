package io.eventuate.coordination.leadership.zookeeper;

import io.eventuate.coordination.leadership.tests.AbstractLeadershipTest;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;

import java.util.UUID;

public class LeadershipTest extends AbstractLeadershipTest<ZkLeaderSelector> {

  private String lockId;

  @Before
  public void init() {
    lockId = String.format("/zk/lock/test/%s", UUID.randomUUID().toString());
  }

  @Override
  protected ZkLeaderSelector createLeaderSelector(Runnable leaderSelectedCallback, Runnable leaderRemovedCallback) {
    String zkUrl = System.getenv("EVENTUATELOCAL_ZOOKEEPER_CONNECTION_STRING");

    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkUrl,
            new ExponentialBackoffRetry(1000, 5));
    curatorFramework.start();

    return new ZkLeaderSelector(curatorFramework,
            lockId,
            leaderSelectedCallback,
            leaderRemovedCallback);
  }
}
