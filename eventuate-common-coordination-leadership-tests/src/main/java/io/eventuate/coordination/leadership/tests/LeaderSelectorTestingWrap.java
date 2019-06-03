package io.eventuate.coordination.leadership.tests;

import io.eventuate.coordination.leadership.EventuateLeaderSelector;
import io.eventuate.util.test.async.Eventually;
import org.junit.Assert;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderSelectorTestingWrap<SELECTOR extends EventuateLeaderSelector> {
  private SELECTOR selector;
  private AtomicInteger invocationCounter;
  private AtomicBoolean leaderFlag;

  public LeaderSelectorTestingWrap(SELECTOR selector, AtomicInteger invocationCounter, AtomicBoolean leaderFlag) {
    this.selector = selector;
    this.invocationCounter = invocationCounter;
    this.leaderFlag = leaderFlag;
  }

  public void eventuallyAssertIsLeaderAndCallbackIsInvokedOnce() {
    Eventually.eventually(() -> {
      Assert.assertTrue(isLeader());
      Assert.assertEquals(1, getInvocationCount());
    });
  }

  public void eventuallyAssertIsNotLeaderAndCallbackIsInvokedOnce() {
    Eventually.eventually(() -> {
      Assert.assertFalse(isLeader());
      Assert.assertEquals(1, getInvocationCount());
    });
  }

  public int getInvocationCount() {
    return invocationCounter.get();
  }

  public boolean isLeader() {
    return leaderFlag.get();
  }

  public SELECTOR getSelector() {
    return selector;
  }

  public void start() {
    selector.start();
  }

  public void stop() {
    selector.stop();
  }
}
