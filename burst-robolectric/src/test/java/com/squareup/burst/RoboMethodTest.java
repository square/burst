package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BurstRobolectric.class)
@Config(manifest=Config.NONE)
public final class RoboMethodTest {
  public RoboMethodTest() { }

  @Test public void none() {
    assertTrue(true);
  }

  @Test public void single(RoboSoda soda) {
    assertNotNull(soda);
  }

  // tests that the correct method is called when two have the same name but different signatures
  @Test public void single(RoboSoda soda, RoboSnack snack) {
    assertNotNull(soda);
    assertNotNull(snack);
  }

  @Test public void multiple(RoboSoda soda, RoboSnack snack) {
    assertNotNull(soda);
    assertNotNull(snack);
  }
}
