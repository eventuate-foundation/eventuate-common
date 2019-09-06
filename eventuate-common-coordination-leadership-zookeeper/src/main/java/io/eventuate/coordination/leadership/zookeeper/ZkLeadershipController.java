package io.eventuate.coordination.leadership.zookeeper;

import io.eventuate.coordination.leadership.LeadershipController;

import java.util.concurrent.CountDownLatch;

public class ZkLeadershipController implements LeadershipController {

  private CountDownLatch stopCountDownLatch;

  public ZkLeadershipController(CountDownLatch stopCountDownLatch) {
    this.stopCountDownLatch = stopCountDownLatch;
  }

  @Override
  public void stop() {
    stopCountDownLatch.countDown();
  }
}
