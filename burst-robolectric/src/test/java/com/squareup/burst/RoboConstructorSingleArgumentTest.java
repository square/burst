package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstRobolectric.class)
public final class RoboConstructorSingleArgumentTest {
  private final RoboSoda soda;

  public RoboConstructorSingleArgumentTest(RoboSoda soda) {
    this.soda = soda;
  }

  @Test public void testMethod() {
    assertNotNull(soda);
  }
}
