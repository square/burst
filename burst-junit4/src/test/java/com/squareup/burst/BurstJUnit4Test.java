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
        "START testMethod(com.squareup.burst.ConstructorSingleArgumentTest[PEPSI])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleArgumentTest[PEPSI])",
        "START testMethod(com.squareup.burst.ConstructorSingleArgumentTest[COKE])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleArgumentTest[COKE])",
        "START testMethod(com.squareup.burst.ConstructorSingleArgumentTest[RC_COLA])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleArgumentTest[RC_COLA])");
  }

  @Test public void constructorSingleNamed() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorSingleNamedArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.ConstructorSingleNamedArgumentTest[Drink=PEPSI])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleNamedArgumentTest[Drink=PEPSI])",
        "START testMethod(com.squareup.burst.ConstructorSingleNamedArgumentTest[Drink=COKE])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleNamedArgumentTest[Drink=COKE])",
        "START testMethod(com.squareup.burst.ConstructorSingleNamedArgumentTest[Drink=RC_COLA])",
        "FINISH testMethod(com.squareup.burst.ConstructorSingleNamedArgumentTest[Drink=RC_COLA])");
  }

  @Test public void constructorMultiple() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorMultipleArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[PEPSI, CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[PEPSI, CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[PEPSI, NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[PEPSI, NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[PEPSI, CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[PEPSI, CANDY])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[COKE, CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[COKE, CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[COKE, NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[COKE, NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[COKE, CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[COKE, CANDY])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[RC_COLA, CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[RC_COLA, CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[RC_COLA, NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[RC_COLA, NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[RC_COLA, CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleArgumentTest[RC_COLA, CANDY])");
  }

  @Test public void constructorMultipleNames() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorMultipleNamedArgumentTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=PEPSI, CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=PEPSI, CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=PEPSI, NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=PEPSI, NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=PEPSI, CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=PEPSI, CANDY])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=COKE, CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=COKE, CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=COKE, NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=COKE, NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=COKE, CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=COKE, CANDY])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=RC_COLA, CHIPS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=RC_COLA, CHIPS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=RC_COLA, NUTS])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=RC_COLA, NUTS])",
        "START testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=RC_COLA, CANDY])",
        "FINISH testMethod(com.squareup.burst.ConstructorMultipleNamedArgumentTest[Drink=RC_COLA, CANDY])");
  }

  @Test public void method() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(MethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START single[PEPSI](com.squareup.burst.MethodTest)",
        "FINISH single[PEPSI](com.squareup.burst.MethodTest)",
        "START single[COKE](com.squareup.burst.MethodTest)",
        "FINISH single[COKE](com.squareup.burst.MethodTest)",
        "START single[RC_COLA](com.squareup.burst.MethodTest)",
        "FINISH single[RC_COLA](com.squareup.burst.MethodTest)",
        "START none(com.squareup.burst.MethodTest)",
        "FINISH none(com.squareup.burst.MethodTest)",
        "START multiple[PEPSI, CHIPS](com.squareup.burst.MethodTest)",
        "FINISH multiple[PEPSI, CHIPS](com.squareup.burst.MethodTest)",
        "START multiple[PEPSI, NUTS](com.squareup.burst.MethodTest)",
        "FINISH multiple[PEPSI, NUTS](com.squareup.burst.MethodTest)",
        "START multiple[PEPSI, CANDY](com.squareup.burst.MethodTest)",
        "FINISH multiple[PEPSI, CANDY](com.squareup.burst.MethodTest)",
        "START multiple[COKE, CHIPS](com.squareup.burst.MethodTest)",
        "FINISH multiple[COKE, CHIPS](com.squareup.burst.MethodTest)",
        "START multiple[COKE, NUTS](com.squareup.burst.MethodTest)",
        "FINISH multiple[COKE, NUTS](com.squareup.burst.MethodTest)",
        "START multiple[COKE, CANDY](com.squareup.burst.MethodTest)",
        "FINISH multiple[COKE, CANDY](com.squareup.burst.MethodTest)",
        "START multiple[RC_COLA, CHIPS](com.squareup.burst.MethodTest)",
        "FINISH multiple[RC_COLA, CHIPS](com.squareup.burst.MethodTest)",
        "START multiple[RC_COLA, NUTS](com.squareup.burst.MethodTest)",
        "FINISH multiple[RC_COLA, NUTS](com.squareup.burst.MethodTest)",
        "START multiple[RC_COLA, CANDY](com.squareup.burst.MethodTest)",
        "FINISH multiple[RC_COLA, CANDY](com.squareup.burst.MethodTest)");
  }

  @Test public void namedMethod() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(NamedMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START single[Drink=PEPSI](com.squareup.burst.NamedMethodTest)",
        "FINISH single[Drink=PEPSI](com.squareup.burst.NamedMethodTest)",
        "START single[Drink=COKE](com.squareup.burst.NamedMethodTest)",
        "FINISH single[Drink=COKE](com.squareup.burst.NamedMethodTest)",
        "START single[Drink=RC_COLA](com.squareup.burst.NamedMethodTest)",
        "FINISH single[Drink=RC_COLA](com.squareup.burst.NamedMethodTest)",
        "START none(com.squareup.burst.NamedMethodTest)",
        "FINISH none(com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=PEPSI, CHIPS](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=PEPSI, CHIPS](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=PEPSI, NUTS](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=PEPSI, NUTS](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=PEPSI, CANDY](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=PEPSI, CANDY](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=COKE, CHIPS](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=COKE, CHIPS](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=COKE, NUTS](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=COKE, NUTS](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=COKE, CANDY](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=COKE, CANDY](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=RC_COLA, CHIPS](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=RC_COLA, CHIPS](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=RC_COLA, NUTS](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=RC_COLA, NUTS](com.squareup.burst.NamedMethodTest)",
        "START multiple[Drink=RC_COLA, CANDY](com.squareup.burst.NamedMethodTest)",
        "FINISH multiple[Drink=RC_COLA, CANDY](com.squareup.burst.NamedMethodTest)");
  }

  @Test public void constructorAndMethod() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(ConstructorAndMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START single[CHIPS](com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "FINISH single[CHIPS](com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "START single[NUTS](com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "FINISH single[NUTS](com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "START single[CANDY](com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "FINISH single[CANDY](com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "START none(com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "FINISH none(com.squareup.burst.ConstructorAndMethodTest[PEPSI])",
        "START single[CHIPS](com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "FINISH single[CHIPS](com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "START single[NUTS](com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "FINISH single[NUTS](com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "START single[CANDY](com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "FINISH single[CANDY](com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "START none(com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "FINISH none(com.squareup.burst.ConstructorAndMethodTest[COKE])",
        "START single[CHIPS](com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "FINISH single[CHIPS](com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "START single[NUTS](com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "FINISH single[NUTS](com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "START single[CANDY](com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "FINISH single[CANDY](com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "START none(com.squareup.burst.ConstructorAndMethodTest[RC_COLA])",
        "FINISH none(com.squareup.burst.ConstructorAndMethodTest[RC_COLA])");
  }

  @Test public void singleField() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(SingleFieldTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.SingleFieldTest[PEPSI])",
        "FINISH testMethod(com.squareup.burst.SingleFieldTest[PEPSI])",
        "START testMethod(com.squareup.burst.SingleFieldTest[COKE])",
        "FINISH testMethod(com.squareup.burst.SingleFieldTest[COKE])",
        "START testMethod(com.squareup.burst.SingleFieldTest[RC_COLA])",
        "FINISH testMethod(com.squareup.burst.SingleFieldTest[RC_COLA])");
  }

  @Test public void multipleFields() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(MultipleFieldsTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.MultipleFieldsTest[PEPSI, CHIPS])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[PEPSI, CHIPS])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[PEPSI, NUTS])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[PEPSI, NUTS])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[PEPSI, CANDY])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[PEPSI, CANDY])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[COKE, CHIPS])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[COKE, CHIPS])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[COKE, NUTS])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[COKE, NUTS])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[COKE, CANDY])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[COKE, CANDY])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[RC_COLA, CHIPS])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[RC_COLA, CHIPS])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[RC_COLA, NUTS])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[RC_COLA, NUTS])",
        "START testMethod(com.squareup.burst.MultipleFieldsTest[RC_COLA, CANDY])",
        "FINISH testMethod(com.squareup.burst.MultipleFieldsTest[RC_COLA, CANDY])");
  }

  @Test public void multipleNamedFields() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(MultipleNamedFieldsTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=PEPSI, CHIPS])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=PEPSI, CHIPS])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=PEPSI, NUTS])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=PEPSI, NUTS])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=PEPSI, CANDY])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=PEPSI, CANDY])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=COKE, CHIPS])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=COKE, CHIPS])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=COKE, NUTS])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=COKE, NUTS])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=COKE, CANDY])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=COKE, CANDY])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=RC_COLA, CHIPS])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=RC_COLA, CHIPS])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=RC_COLA, NUTS])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=RC_COLA, NUTS])",
        "START testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=RC_COLA, CANDY])",
        "FINISH testMethod(com.squareup.burst.MultipleNamedFieldsTest[Drink=RC_COLA, CANDY])");
  }

  @Test public void fieldAndMethod() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(FieldAndMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod[PEPSI](com.squareup.burst.FieldAndMethodTest[CHIPS])",
        "FINISH testMethod[PEPSI](com.squareup.burst.FieldAndMethodTest[CHIPS])",
        "START testMethod[COKE](com.squareup.burst.FieldAndMethodTest[CHIPS])",
        "FINISH testMethod[COKE](com.squareup.burst.FieldAndMethodTest[CHIPS])",
        "START testMethod[RC_COLA](com.squareup.burst.FieldAndMethodTest[CHIPS])",
        "FINISH testMethod[RC_COLA](com.squareup.burst.FieldAndMethodTest[CHIPS])",
        "START testMethod[PEPSI](com.squareup.burst.FieldAndMethodTest[NUTS])",
        "FINISH testMethod[PEPSI](com.squareup.burst.FieldAndMethodTest[NUTS])",
        "START testMethod[COKE](com.squareup.burst.FieldAndMethodTest[NUTS])",
        "FINISH testMethod[COKE](com.squareup.burst.FieldAndMethodTest[NUTS])",
        "START testMethod[RC_COLA](com.squareup.burst.FieldAndMethodTest[NUTS])",
        "FINISH testMethod[RC_COLA](com.squareup.burst.FieldAndMethodTest[NUTS])",
        "START testMethod[PEPSI](com.squareup.burst.FieldAndMethodTest[CANDY])",
        "FINISH testMethod[PEPSI](com.squareup.burst.FieldAndMethodTest[CANDY])",
        "START testMethod[COKE](com.squareup.burst.FieldAndMethodTest[CANDY])",
        "FINISH testMethod[COKE](com.squareup.burst.FieldAndMethodTest[CANDY])",
        "START testMethod[RC_COLA](com.squareup.burst.FieldAndMethodTest[CANDY])",
        "FINISH testMethod[RC_COLA](com.squareup.burst.FieldAndMethodTest[CANDY])");
  }

  @Test public void namedFieldAndNamedMethod() throws InitializationError {
    BurstJUnit4 runner = new BurstJUnit4(NamedFieldAndNamedMethodTest.class);
    runner.run(listener.notifier());
    assertThat(listener.journal()).containsExactly(
        "START testMethod[Drink=PEPSI](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CHIPS])",
        "FINISH testMethod[Drink=PEPSI](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CHIPS])",
        "START testMethod[Drink=COKE](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CHIPS])",
        "FINISH testMethod[Drink=COKE](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CHIPS])",
        "START testMethod[Drink=RC_COLA](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CHIPS])",
        "FINISH testMethod[Drink=RC_COLA](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CHIPS])",
        "START testMethod[Drink=PEPSI](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=NUTS])",
        "FINISH testMethod[Drink=PEPSI](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=NUTS])",
        "START testMethod[Drink=COKE](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=NUTS])",
        "FINISH testMethod[Drink=COKE](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=NUTS])",
        "START testMethod[Drink=RC_COLA](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=NUTS])",
        "FINISH testMethod[Drink=RC_COLA](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=NUTS])",
        "START testMethod[Drink=PEPSI](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CANDY])",
        "FINISH testMethod[Drink=PEPSI](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CANDY])",
        "START testMethod[Drink=COKE](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CANDY])",
        "FINISH testMethod[Drink=COKE](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CANDY])",
        "START testMethod[Drink=RC_COLA](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CANDY])",
        "FINISH testMethod[Drink=RC_COLA](com.squareup.burst.NamedFieldAndNamedMethodTest[Food=CANDY])");
  }
}
