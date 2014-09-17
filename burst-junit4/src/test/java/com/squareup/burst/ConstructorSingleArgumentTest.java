package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstJUnit4.class)
public final class ConstructorSingleArgumentTest {
  private final Soda soda;

  public ConstructorSingleArgumentTest(Soda soda) {
    this.soda = soda;
  }

  @Test public void testMethod() {
    assertNotNull(soda);
  }
}
