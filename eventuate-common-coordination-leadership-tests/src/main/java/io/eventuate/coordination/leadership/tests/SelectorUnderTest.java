package io.eventuate.coordination.leadership.tests;

import io.eventuate.coordination.leadership.EventuateLeaderSelector;
import io.eventuate.coordination.leadership.LeaderSelectedCallback;
import io.eventuate.coordination.leadership.LeadershipController;
import io.eventuate.util.test.async.Eventually;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class SelectorUnderTest<SELECTOR extends EventuateLeaderSelector> {
  private SELECTOR selector;
  private final AtomicInteger invocationCounter = new AtomicInteger(0);;
  private final AtomicBoolean leaderFlag = new AtomicBoolean(false);
  private LeadershipController leadershipController;

  private CountDownLatch sleepLatch;

  public SelectorUnderTest(BiFunction<LeaderSelectedCallback , Runnable, SELECTOR> factory) {
    this.selector = factory.apply(this::leadershipSelectedCallback, this::leadershipRemovedCallback);
  }

  private void leadershipRemovedCallback() {
    leaderFlag.set(false);
  }

  private void leadershipSelectedCallback(LeadershipController leadershipController) {
    this.leadershipController = leadershipController;
    leaderFlag.set(true);
    invocationCounter.incrementAndGet();
    sleepLatch= new CountDownLatch(1);
    try {
      sleepLatch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void eventuallyAssertIsLeaderAndCallbackIsInvokedOnce() {
    Eventually.eventually(() -> {
      Assertions.assertTrue(isLeader(), "should be leader");
      Assertions.assertEquals(1, getInvocationCount());
    });
  }

  public void eventuallyAssertIsNotLeaderAndCallbackIsInvokedOnce() {
    Eventually.eventually(() -> {
      Assertions.assertFalse(isLeader(), "should not be leader");
      Assertions.assertEquals(1, getInvocationCount());
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

  public void relinquish() {
    Assertions.assertTrue(isLeader(), "should be leader");
    sleepLatch.countDown();
    leadershipController.stop();
  }

  public void eventuallyAssertIsLeaderAndCallbackIsInvokedTwice() {
    Eventually.eventually(() -> {
      Assertions.assertTrue(isLeader(), "should be leader");
      Assertions.assertEquals(2, getInvocationCount());
    });

  }
}
