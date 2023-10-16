package io.eventuate.coordination.leadership.tests;

import io.eventuate.coordination.leadership.EventuateLeaderSelector;
import io.eventuate.coordination.leadership.LeaderSelectedCallback;
import io.eventuate.coordination.leadership.LeadershipController;
import io.eventuate.util.test.async.Eventually;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractLeadershipTest <SELECTOR extends EventuateLeaderSelector> {

  private LeadershipController leadershipController;

  @Test
  public void testThatCallbackInvokedOnce1() {
    SelectorUnderTest<SELECTOR> selector = createAndStartLeaderSelector();

    selector.eventuallyAssertIsLeaderAndCallbackIsInvokedOnce();
    selector.stop();
    selector.eventuallyAssertIsNotLeaderAndCallbackIsInvokedOnce();
  }

  @Test
  public void testThatLeaderIsChangedWhenStopped() {
    SelectorUnderTest<SELECTOR> selector1 = createAndStartLeaderSelector();
    SelectorUnderTest<SELECTOR> selector2 = createAndStartLeaderSelector();

    eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(selector1, selector2);

    SelectorUnderTest<SELECTOR> instanceWhichBecameLeaderFirst =
            selector1.isLeader() ? selector1 : selector2;

    SelectorUnderTest<SELECTOR> instanceWhichBecameLeaderLast =
            selector2.isLeader() ? selector1 : selector2;

    instanceWhichBecameLeaderFirst.stop();

    instanceWhichBecameLeaderLast.eventuallyAssertIsLeaderAndCallbackIsInvokedOnce();

    instanceWhichBecameLeaderLast.stop();
  }

  @Test
  public void testThatOnlyOneLeaderIsWorkingInTheSameTime() throws Exception {
    SelectorUnderTest<SELECTOR> selector1 = createAndStartLeaderSelector();
    SelectorUnderTest<SELECTOR> selector2 = createAndStartLeaderSelector();

    eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(selector1, selector2);

    Thread.sleep(3000);

    eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(selector1, selector2);

    selector1.stop();
    selector2.stop();
  }

  @Test
  public void testRestart() {
    SelectorUnderTest<SELECTOR> selector = createAndStartLeaderSelector();

    selector.eventuallyAssertIsLeaderAndCallbackIsInvokedOnce();
    selector.relinquish();
    selector.eventuallyAssertIsLeaderAndCallbackIsInvokedTwice();
  }


  protected void eventuallyAssertLeadershipIsAssignedOnlyForOneSelector(SelectorUnderTest<SELECTOR> selector1,
                                                                        SelectorUnderTest<SELECTOR> selector2) {
    Eventually.eventually(() -> {
      boolean leader1Condition = selector1.isLeader() && !selector2.isLeader();
      boolean leader2Condition = selector2.isLeader() && !selector1.isLeader();
      Assert.assertTrue(leader1Condition || leader2Condition);
    });
  }

  protected SelectorUnderTest<SELECTOR> createAndStartLeaderSelector() {
    SelectorUnderTest<SELECTOR> selector = new SelectorUnderTest<>(this::createLeaderSelector);
    selector.start();
    return selector;
  }

  protected abstract SELECTOR createLeaderSelector(LeaderSelectedCallback leaderSelectedCallback, Runnable leaderRemovedCallback);
}
