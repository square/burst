package com.example.burst;

import com.squareup.burst.annotation.Burst;
import junit.framework.TestCase;

public class FieldTest extends TestCase {
  @Burst Drink drink1;
  @Burst Drink drink2;

  public void testMultipleFields() {
    assertNotNull(drink1);
    assertNotNull(drink2);
  }

  public void testFieldsAndParameter(Drink drink3) {
    // TODO method parameters are currently not supported in Android tests!
    assertNotNull(drink1);
    assertNotNull(drink2);
    assertNotNull(drink3);
  }
}
