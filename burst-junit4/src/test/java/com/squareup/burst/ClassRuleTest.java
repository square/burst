package com.squareup.burst;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BurstJUnit4.class)
public class ClassRuleTest {
  @ClassRule public static TestRule rule = new ExternalResource() {
    @Override protected void before() throws Throwable {
      assertFalse(beforeCalled);
      beforeCalled = true;
    }

    @Override protected void after() {
      assertTrue(beforeCalled);
      assertFalse(afterCalled);
      afterCalled = true;
    }
  };

  private static boolean beforeCalled = false;
  private static boolean afterCalled = false;

  private final Snack snack;

  public ClassRuleTest(Snack snack) {
    this.snack = snack;
  }

  @Test public void testSnackIsSet() {
    assertTrue(beforeCalled);
    assertFalse(afterCalled);
    assertNotNull(snack);
  }
}
