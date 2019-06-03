package io.eventuate.coordination.leadership.tests;

import io.eventuate.coordination.leadership.EventuateLeaderSelector;
import io.eventuate.util.test.async.Eventually;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractLeadershipTest <SELECTOR extends EventuateLeaderSelector> {
  @Test
  public void testThatCallbackInvokedOnce() throws Exception {
    AtomicInteger callbackInvocationCounter = new AtomicInteger(0);

    EventuateLeaderSelector eventuateLeaderSelector = createLeaderSelector(callbackInvocationCounter);

    Thread.sleep(1000);

    Assert.assertEquals(1, callbackInvocationCounter.get());

    eventuateLeaderSelector.stop();
  }

  @Test
  public void testThatLeaderChangedWhenStopped() throws Exception {
    AtomicInteger callbackInvocationCounterForLeader1 = new AtomicInteger(0);
    AtomicInteger callbackInvocationCounterForLeader2 = new AtomicInteger(0);

    EventuateLeaderSelector eventuateLeaderSelector1 = createLeaderSelector(callbackInvocationCounterForLeader1);
    EventuateLeaderSelector eventuateLeaderSelector2 = createLeaderSelector(callbackInvocationCounterForLeader2);

    assertLeadershipWasAssignedForOneSelector(callbackInvocationCounterForLeader1, callbackInvocationCounterForLeader2);

    boolean leader1 = callbackInvocationCounterForLeader1.get() == 1;

    if (leader1) {
      eventuateLeaderSelector1.stop();
    } else {
      eventuateLeaderSelector2.stop();
    }

    assertLeadershipWasAssignedForBothSelectors(callbackInvocationCounterForLeader1, callbackInvocationCounterForLeader2);

    if (leader1) {
      eventuateLeaderSelector2.stop();
    } else {
      eventuateLeaderSelector1.stop();
    }
  }

  @Test
  public void testThatOnlyOneLeaderWorkInTheSameTime() throws Exception {
    AtomicInteger callbackInvocationCounterForLeader1 = new AtomicInteger(0);
    AtomicInteger callbackInvocationCounterForLeader2 = new AtomicInteger(0);

    createLeaderSelector(callbackInvocationCounterForLeader1, true);
    createLeaderSelector(callbackInvocationCounterForLeader2, true);

    Thread.sleep(3000);

    assertLeadershipWasAssignedForOneSelector(callbackInvocationCounterForLeader1, callbackInvocationCounterForLeader2);
  }


  protected void assertLeadershipWasAssignedForOneSelector(AtomicInteger invocationCounter1, AtomicInteger invocationCounter2) {
    Eventually.eventually(() -> {
      boolean leader1Condition = invocationCounter1.get() == 1 && invocationCounter2.get() == 0;
      boolean leader2Condition = invocationCounter2.get() == 1 && invocationCounter1.get() == 0;
      Assert.assertTrue(leader1Condition || leader2Condition);
    });
  }

  protected void assertLeadershipWasAssignedForBothSelectors(AtomicInteger invocationCounter1, AtomicInteger invocationCounter2) {
    Eventually.eventually(() -> {
      Assert.assertEquals(1, invocationCounter1.get());
      Assert.assertEquals(1, invocationCounter2.get());
    });
  }

  protected SELECTOR createLeaderSelector(AtomicInteger invocationCounter) {
    return createLeaderSelector(invocationCounter, false);
  }

  protected abstract SELECTOR createLeaderSelector(AtomicInteger invocationCounter, boolean infinite);
}
