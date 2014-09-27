package com.example.burst;

import junit.framework.TestCase;

public class IgnoredMethodTest extends TestCase {
  public void testNothingInParticular() {
  }

  @Ignore public void testIgnored() {
    fail("This test should have been skipped.");
  }
}
