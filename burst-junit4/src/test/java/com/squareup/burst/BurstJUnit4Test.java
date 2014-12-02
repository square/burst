package com.squareup.burst;

import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

public final class BurstJUnit4Test {
  private final JournalingListener listener = new JournalingListener();

  @Test public void constructorNone() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorNoArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.ConstructorNoArgumentTest)",
        "FINISH testMethod(com.squareup.burst.ConstructorNoArgumentTest)");
  }

  @Test public void constructorSingle() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorSingleArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.ConstructorSingleArgumentTest[Soda.PEPSI])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleArgumentTest[Soda.PEPSI])",
        "START testMethod(com.squareup.burst.ConstructorSingleArgumentTest[Soda.COKE])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleArgumentTest[Soda.COKE])",
        "START testMethod(com.squareup.burst.ConstructorSingleArgumentTest[Soda.RC_COLA])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleArgumentTest[Soda.RC_COLA])");
  }

  @Test public void constructorMultiple() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorMultipleArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.PEPSI, Snack.CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.PEPSI, Snack.CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.PEPSI, Snack.NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.PEPSI, Snack.NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.PEPSI, Snack.CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.PEPSI, Snack.CANDY])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.COKE, Snack.CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.COKE, Snack.CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.COKE, Snack.NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.COKE, Snack.NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.COKE, Snack.CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.COKE, Snack.CANDY])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.RC_COLA, Snack.CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.RC_COLA, Snack.CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.RC_COLA, Snack.NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.RC_COLA, Snack.NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.RC_COLA, Snack.CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[Soda.RC_COLA, Snack.CANDY])");
  }

  @Test public void method() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(MethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START single[Soda.PEPSI](com.squareup.burst.MethodTest)",
        "FINISH single[Soda.PEPSI](com.squareup.burst.MethodTest)",
        "START single[Soda.COKE](com.squareup.burst.MethodTest)",
        "FINISH single[Soda.COKE](com.squareup.burst.MethodTest)",
        "START single[Soda.RC_COLA](com.squareup.burst.MethodTest)",
        "FINISH single[Soda.RC_COLA](com.squareup.burst.MethodTest)",
        "START none(com.squareup.burst.MethodTest)",
        "FINISH none(com.squareup.burst.MethodTest)",
        "START multiple[Soda.PEPSI, Snack.CHIPS](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.PEPSI, Snack.CHIPS](com.squareup.burst.MethodTest)",
        "START multiple[Soda.PEPSI, Snack.NUTS](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.PEPSI, Snack.NUTS](com.squareup.burst.MethodTest)",
        "START multiple[Soda.PEPSI, Snack.CANDY](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.PEPSI, Snack.CANDY](com.squareup.burst.MethodTest)",
        "START multiple[Soda.COKE, Snack.CHIPS](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.COKE, Snack.CHIPS](com.squareup.burst.MethodTest)",
        "START multiple[Soda.COKE, Snack.NUTS](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.COKE, Snack.NUTS](com.squareup.burst.MethodTest)",
        "START multiple[Soda.COKE, Snack.CANDY](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.COKE, Snack.CANDY](com.squareup.burst.MethodTest)",
        "START multiple[Soda.RC_COLA, Snack.CHIPS](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.RC_COLA, Snack.CHIPS](com.squareup.burst.MethodTest)",
        "START multiple[Soda.RC_COLA, Snack.NUTS](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.RC_COLA, Snack.NUTS](com.squareup.burst.MethodTest)",
        "START multiple[Soda.RC_COLA, Snack.CANDY](com.squareup.burst.MethodTest)",
        "FINISH multiple[Soda.RC_COLA, Snack.CANDY](com.squareup.burst.MethodTest)");
  }

  @Test public void constructorAndMethod() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorAndMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START single[Snack.CHIPS](com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "FINISH single[Snack.CHIPS](com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "START single[Snack.NUTS](com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "FINISH single[Snack.NUTS](com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "START single[Snack.CANDY](com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "FINISH single[Snack.CANDY](com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "START none(com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "FINISH none(com.squareup.burst.ConstructorAndMethodTest[Soda.PEPSI])",
        "START single[Snack.CHIPS](com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "FINISH single[Snack.CHIPS](com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "START single[Snack.NUTS](com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "FINISH single[Snack.NUTS](com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "START single[Snack.CANDY](com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "FINISH single[Snack.CANDY](com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "START none(com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "FINISH none(com.squareup.burst.ConstructorAndMethodTest[Soda.COKE])",
        "START single[Snack.CHIPS](com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "FINISH single[Snack.CHIPS](com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "START single[Snack.NUTS](com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "FINISH single[Snack.NUTS](com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "START single[Snack.CANDY](com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "FINISH single[Snack.CANDY](com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "START none(com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])",
        "FINISH none(com.squareup.burst.ConstructorAndMethodTest[Soda.RC_COLA])");
  }
}
