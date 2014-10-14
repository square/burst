package com.squareup.burst;

import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class BurstRoboTest {
  private final RoboJournalingListener listener = new RoboJournalingListener();

  enum First { APPLE, BEARD, COUCH }
  enum Second { DINGO, EAGLE }

  public static class One {
    public One(First first) {}
  }

  public static class None {}
  public static class Empty {
    public Empty() {}
  }
  public static class NonPublic {
    NonPublic() {}
  }
  public static class Multiple {
    public Multiple(First first) {}
    public Multiple(Second second) {}
  }

  @Test public void noConstructor() {
    Constructor<?> constructor = BurstJUnit4.findConstructor(None.class);
    assertThat(constructor).isNotNull();
    assertThat(constructor.getParameterTypes()).isEmpty();
  }

  @Test public void nonPublicConstructor() {
    try {
      BurstJUnit4.findConstructor(NonPublic.class);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          NonPublic.class.getName() + " requires a single public constructor.");
    }
  }

  @Test public void singleConstructor() {
    Constructor<?> constructor = BurstJUnit4.findConstructor(One.class);
    assertThat(constructor).isNotNull();
    assertThat(constructor.getParameterTypes()).containsExactly(First.class);
  }

  @Test public void multipleConstructors() {
    try {
      BurstJUnit4.findConstructor(Multiple.class);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(Multiple.class.getName() + " requires a single public constructor.");
    }
  }

  @Test public void constructorNone() throws InitializationError {
    BurstRobolectric runner = new BurstRobolectric(RoboConstructorNoArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.RoboConstructorNoArgumentTest)",
        "FINISH testMethod(com.squareup.burst.RoboConstructorNoArgumentTest)");
  }

  @Test public void constructorSingle() throws InitializationError {
    BurstRobolectric runner = new BurstRobolectric(RoboConstructorSingleArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.RoboConstructorSingleArgumentTest[RoboSoda.PEPSI])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorSingleArgumentTest[RoboSoda.PEPSI])",
        "START testMethod(com.squareup.burst.RoboConstructorSingleArgumentTest[RoboSoda.COKE])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorSingleArgumentTest[RoboSoda.COKE])",
        "START testMethod(com.squareup.burst.RoboConstructorSingleArgumentTest[RoboSoda.RC_COLA])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorSingleArgumentTest[RoboSoda.RC_COLA])");
  }

  @Test public void constructorMultiple() throws InitializationError {
    BurstRobolectric runner = new BurstRobolectric(RoboConstructorMultipleArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.PEPSI, RoboSnack.CHIPS])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.PEPSI, RoboSnack.CHIPS])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.PEPSI, RoboSnack.NUTS])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.PEPSI, RoboSnack.NUTS])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.PEPSI, RoboSnack.CANDY])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.PEPSI, RoboSnack.CANDY])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.COKE, RoboSnack.CHIPS])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.COKE, RoboSnack.CHIPS])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.COKE, RoboSnack.NUTS])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.COKE, RoboSnack.NUTS])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.COKE, RoboSnack.CANDY])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.COKE, RoboSnack.CANDY])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.RC_COLA, RoboSnack.CHIPS])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.RC_COLA, RoboSnack.CHIPS])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.RC_COLA, RoboSnack.NUTS])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.RC_COLA, RoboSnack.NUTS])",
        "START testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.RC_COLA, RoboSnack.CANDY])",
        "FINISH testMethod(com.squareup.burst.RoboConstructorMultipleArgumentTest[RoboSoda.RC_COLA, RoboSnack.CANDY])");
  }

  @Test public void method() throws InitializationError {
    BurstRobolectric runner = new BurstRobolectric(RoboMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START single[RoboSoda.PEPSI](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.PEPSI](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.COKE](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.COKE](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.RC_COLA](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.RC_COLA](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.PEPSI, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.PEPSI, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.PEPSI, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.PEPSI, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.PEPSI, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.PEPSI, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.COKE, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.COKE, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.COKE, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.COKE, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.COKE, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.COKE, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.RC_COLA, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.RC_COLA, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.RC_COLA, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.RC_COLA, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "START single[RoboSoda.RC_COLA, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "FINISH single[RoboSoda.RC_COLA, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "START none(com.squareup.burst.RoboMethodTest)",
        "FINISH none(com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.PEPSI, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.PEPSI, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.PEPSI, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.PEPSI, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.PEPSI, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.PEPSI, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.COKE, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.COKE, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.COKE, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.COKE, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.COKE, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.COKE, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.RC_COLA, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.RC_COLA, RoboSnack.CHIPS](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.RC_COLA, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.RC_COLA, RoboSnack.NUTS](com.squareup.burst.RoboMethodTest)",
        "START multiple[RoboSoda.RC_COLA, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)",
        "FINISH multiple[RoboSoda.RC_COLA, RoboSnack.CANDY](com.squareup.burst.RoboMethodTest)");
  }

  @Test public void constructorAndMethod() throws InitializationError {
    BurstRobolectric runner = new BurstRobolectric(RoboConstructorAndMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod[RoboSnack.CHIPS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.PEPSI])",
        "FINISH testMethod[RoboSnack.CHIPS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.PEPSI])",
        "START testMethod[RoboSnack.NUTS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.PEPSI])",
        "FINISH testMethod[RoboSnack.NUTS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.PEPSI])",
        "START testMethod[RoboSnack.CANDY](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.PEPSI])",
        "FINISH testMethod[RoboSnack.CANDY](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.PEPSI])",
        "START testMethod[RoboSnack.CHIPS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.COKE])",
        "FINISH testMethod[RoboSnack.CHIPS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.COKE])",
        "START testMethod[RoboSnack.NUTS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.COKE])",
        "FINISH testMethod[RoboSnack.NUTS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.COKE])",
        "START testMethod[RoboSnack.CANDY](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.COKE])",
        "FINISH testMethod[RoboSnack.CANDY](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.COKE])",
        "START testMethod[RoboSnack.CHIPS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.RC_COLA])",
        "FINISH testMethod[RoboSnack.CHIPS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.RC_COLA])",
        "START testMethod[RoboSnack.NUTS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.RC_COLA])",
        "FINISH testMethod[RoboSnack.NUTS](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.RC_COLA])",
        "START testMethod[RoboSnack.CANDY](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.RC_COLA])",
        "FINISH testMethod[RoboSnack.CANDY](com.squareup.burst.RoboConstructorAndMethodTest[RoboSoda.RC_COLA])");
  }
}
