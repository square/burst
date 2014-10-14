package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(BurstRobolectric.class)
public final class RoboConstructorNoArgumentTest {
  @Test public void testMethod() {
    assertTrue(true);
  }
}
