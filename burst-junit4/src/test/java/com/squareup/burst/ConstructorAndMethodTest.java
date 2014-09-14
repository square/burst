package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstJUnit4.class)
public final class ConstructorAndMethodTest {
  private final Soda soda;

  public ConstructorAndMethodTest(Soda soda) {
    this.soda = soda;
  }

  @Test public void testMethod(Snack snack) {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
