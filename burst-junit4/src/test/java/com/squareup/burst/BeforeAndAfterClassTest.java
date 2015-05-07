package com.squareup.burst;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BurstJUnit4.class)
public class BeforeAndAfterClassTest {
  private static boolean beforeClassCalled = false;
  private static boolean afterClassCalled = false;

  private final Snack snack;

  public BeforeAndAfterClassTest(Snack snack) {
    this.snack = snack;
  }

  @BeforeClass public static void beforeClass() {
    assertFalse(beforeClassCalled);
    beforeClassCalled = true;
  }

  @AfterClass public static void afterClass() {
    assertTrue(beforeClassCalled);
    assertFalse(afterClassCalled);
    afterClassCalled = true;
  }

  @Test public void testSnackIsSet() {
    assertTrue(beforeClassCalled);
    assertFalse(afterClassCalled);
    assertNotNull(snack);
  }
}
