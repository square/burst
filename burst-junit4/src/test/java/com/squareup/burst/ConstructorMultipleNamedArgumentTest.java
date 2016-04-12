package com.squareup.burst;

import com.squareup.burst.annotation.Name;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstJUnit4.class)
public final class ConstructorMultipleNamedArgumentTest {
  private final Soda soda;
  private final Snack snack;

  public ConstructorMultipleNamedArgumentTest(@Name("Drink") Soda soda, Snack snack) {
    this.soda = soda;
    this.snack = snack;
  }

  @Test public void testMethod() {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
