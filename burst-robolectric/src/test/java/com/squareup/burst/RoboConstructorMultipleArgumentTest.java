package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstRobolectric.class)
public final class RoboConstructorMultipleArgumentTest {
  private final RoboSoda soda;
  private final RoboSnack snack;

  public RoboConstructorMultipleArgumentTest(RoboSoda soda, RoboSnack snack) {
    this.soda = soda;
    this.snack = snack;
  }

  @Test public void testMethod() {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
