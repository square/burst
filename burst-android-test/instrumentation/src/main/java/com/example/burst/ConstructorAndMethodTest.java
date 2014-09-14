package com.example.burst;

import junit.framework.TestCase;

public final class ConstructorAndMethodTest extends TestCase {
  private final Drink drink1;

  public ConstructorAndMethodTest() {
    // Required for Burst!
    this(null);
  }

  public ConstructorAndMethodTest(Drink drink1) {
    this.drink1 = drink1;
  }

  public void testSingleParameterConstructor() {
    assertNotNull(drink1);
  }

  public void testSingleParameterConstructorAndSingleParameterMethod(Drink drink2) {
    // TODO this currently is not supported!
    assertNotNull(drink1);
    assertNotNull(drink2);
  }
}
