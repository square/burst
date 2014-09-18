package com.example.burst;

import junit.framework.TestCase;

public class ConstructorTest extends TestCase {
  private final Drink drink;

  public ConstructorTest() {
    // Required for Burst!
    this(null);
  }

  public ConstructorTest(Drink drink) {
    this.drink = drink;
  }

  public void testSingleParameterConstructor() {
    assertNotNull(drink);
  }
}
