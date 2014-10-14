package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstRobolectric.class)
public final class RoboConstructorAndMethodTest {
  private final RoboSoda soda;

  public RoboConstructorAndMethodTest(RoboSoda soda) {
    this.soda = soda;
  }

  @Test public void testMethod(RoboSnack snack) {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
