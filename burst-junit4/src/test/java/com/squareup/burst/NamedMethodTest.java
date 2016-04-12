package com.squareup.burst;

import com.squareup.burst.annotation.Name;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BurstJUnit4.class)
public final class NamedMethodTest {
  @Test public void none() {
    assertTrue(true);
  }

  @Test public void single(@Name("Drink") Soda soda) {
    assertNotNull(soda);
  }

  @Test public void multiple(@Name("Drink") Soda soda, Snack snack) {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
