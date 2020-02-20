package io.eventuate.coordination.leadership.zookeeper;

import io.eventuate.coordination.leadership.LeadershipController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class ZkLeadershipController implements LeadershipController {
  private Logger logger = LoggerFactory.getLogger(getClass());

  private CountDownLatch stopCountDownLatch;

  public ZkLeadershipController(CountDownLatch stopCountDownLatch) {
    this.stopCountDownLatch = stopCountDownLatch;
  }

  @Override
  public void stop() {
    logger.info("Stopping leadership controller");
    stopCountDownLatch.countDown();
    logger.info("Stopped leadership controller");
  }
}
