package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstJUnit4.class)
public final class ConstructorMultipleArgumentTest {
  private final Soda soda;
  private final Snack snack;

  public ConstructorMultipleArgumentTest(Soda soda, Snack snack) {
    this.soda = soda;
    this.snack = snack;
  }

  @Test public void testMethod() {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
