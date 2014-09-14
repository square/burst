package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BurstJUnit4.class)
public final class MethodTest {
  @Test public void none() {
    assertTrue(true);
  }

  @Test public void single(Soda soda) {
    assertNotNull(soda);
  }

  @Test public void multiple(Soda soda, Snack snack) {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
