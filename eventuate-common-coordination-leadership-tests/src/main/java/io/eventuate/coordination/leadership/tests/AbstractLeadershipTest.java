package io.eventuate.coordination.leadership.tests;

import io.eventuate.coordination.leadership.EventuateLeaderSelector;
import io.eventuate.util.test.async.Eventually;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractLeadershipTest <SELECTOR extends EventuateLeaderSelector> {

  @Test
  public void testThatCallbackInvokedOnce1() {
    LeaderSelectorTestingWrap<SELECTOR> leaderSelectorTestingWrap = createAndStartLeaderSelector();

    leaderSelectorTestingWrap.eventuallyAssertIsLeaderAndCallbackIsInvokedOnce();
    leaderSelectorTestingWrap.stop();
    leaderSelectorTestingWrap.eventuallyAssertIsNotLeaderAndCallbackIsInvokedOnce();
  }

  @Test
  public void testThatLeaderIsChangedWhenStopped() {
    LeaderSelectorTestingWrap<SELECTOR> leaderSelectorTestingWrap1 = createAndStartLeaderSelector();
    LeaderSelectorTestingWrap<SELECTOR> leaderSelectorTestingWrap2 = createAndStartLeaderSelector();

    eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(leaderSelectorTestingWrap1, leaderSelectorTestingWrap2);

    LeaderSelectorTestingWrap<SELECTOR> instanceWhichBecameLeaderFirst =
            leaderSelectorTestingWrap1.isLeader() ? leaderSelectorTestingWrap1 : leaderSelectorTestingWrap2;

    LeaderSelectorTestingWrap<SELECTOR> instanceWhichBecameLeaderLast =
            leaderSelectorTestingWrap2.isLeader() ? leaderSelectorTestingWrap1 : leaderSelectorTestingWrap2;

    instanceWhichBecameLeaderFirst.stop();

    instanceWhichBecameLeaderLast.eventuallyAssertIsLeaderAndCallbackIsInvokedOnce();

    instanceWhichBecameLeaderLast.stop();
  }

  @Test
  public void testThatOnlyOneLeaderIsWorkingInTheSameTime() throws Exception {
    LeaderSelectorTestingWrap<SELECTOR> leaderSelectorTestingWrap1 = createAndStartLeaderSelector();
    LeaderSelectorTestingWrap<SELECTOR> leaderSelectorTestingWrap2 = createAndStartLeaderSelector();

    eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(leaderSelectorTestingWrap1, leaderSelectorTestingWrap2);

    Thread.sleep(3000);

    eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(leaderSelectorTestingWrap1, leaderSelectorTestingWrap2);

    leaderSelectorTestingWrap1.stop();
    leaderSelectorTestingWrap2.stop();
  }

  protected void eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(LeaderSelectorTestingWrap<SELECTOR> selectorLeaderSelectorTestingWrap1,
                                                                        LeaderSelectorTestingWrap<SELECTOR> selectorLeaderSelectorTestingWrap2) {
    Eventually.eventually(() -> {
      boolean leader1Condition = selectorLeaderSelectorTestingWrap1.isLeader() && !selectorLeaderSelectorTestingWrap2.isLeader();
      boolean leader2Condition = selectorLeaderSelectorTestingWrap2.isLeader() && !selectorLeaderSelectorTestingWrap1.isLeader();
      Assert.assertTrue(leader1Condition || leader2Condition);
    });
  }

  protected LeaderSelectorTestingWrap<SELECTOR> createAndStartLeaderSelector() {
    AtomicInteger invocationCounter = new AtomicInteger(0);
    AtomicBoolean leaderFlag = new AtomicBoolean(false);

    SELECTOR selector = createLeaderSelector(() -> {
      leaderFlag.set(true);
      invocationCounter.incrementAndGet();
      try {
        Thread.sleep(Long.MAX_VALUE);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }, () -> leaderFlag.set(false));

    selector.start();

    return new LeaderSelectorTestingWrap<>(selector, invocationCounter, leaderFlag);
  }

  protected abstract SELECTOR createLeaderSelector(Runnable leaderSelectedCallback, Runnable leaderRemovedCallback);
}
