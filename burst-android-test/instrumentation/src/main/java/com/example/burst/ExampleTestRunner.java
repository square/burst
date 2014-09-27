package com.example.burst;

import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import com.squareup.burst.BurstAndroid;

public final class ExampleTestRunner extends InstrumentationTestRunner {
  private final BurstAndroid testRunner = new IgnoringTestRunner();

  @Override protected AndroidTestRunner getAndroidTestRunner() {
    return testRunner;
  }
}
