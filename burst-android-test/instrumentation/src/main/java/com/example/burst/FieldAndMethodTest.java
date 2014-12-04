package com.example.burst;

import com.squareup.burst.annotation.Burst;
import junit.framework.TestCase;

public class FieldAndMethodTest extends TestCase {
  @Burst Drink drink1;

  public void testFieldAndMethod(Drink drink2) {
    // TODO method parameters are currently not supported in Android tests!
    assertNotNull(drink1);
    assertNotNull(drink2);
  }
}
