package com.example.burst;

import junit.framework.TestCase;

@Ignore
public class IgnoredClassTest extends TestCase {
  public void testIgnored() {
    fail("This test should have been skipped.");
  }
}
