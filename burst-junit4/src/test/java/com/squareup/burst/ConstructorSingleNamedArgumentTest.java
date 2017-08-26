package com.squareup.burst;

import com.squareup.burst.annotation.Name;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(BurstJUnit4.class)
public final class ConstructorSingleNamedArgumentTest {
  private final Soda soda;

  public ConstructorSingleNamedArgumentTest(@Name("Drink") Soda soda) {
    this.soda = soda;
  }

  @Test public void testMethod() {
    assertNotNull(soda);
  }
}
